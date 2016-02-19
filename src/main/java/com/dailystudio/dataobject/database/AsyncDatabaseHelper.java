package com.dailystudio.dataobject.database;

import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import java.util.List;

import com.dailystudio.dataobject.DatabaseObject;
import com.dailystudio.dataobject.query.Query;
import com.dailystudio.development.Logger;

public class AsyncDatabaseHelper {
	
    private static final int EVENT_ARG_INSERT = 1;
    private static final int EVENT_ARG_BULK_INSERT = 2;
    private static final int EVENT_ARG_UPDATE = 3;
    private static final int EVENT_ARG_DELETE = 4;
    private static final int EVENT_ARG_QUERY = 5;
    private static final int EVENT_ARG_QUERY_CURSOR = 6;

    protected static final class WorkerArgs {
        public Handler handler;
        public Query query;
        public Class<? extends DatabaseObject> projectionClass;
        
        public DatabaseObject[] objects;
        public Object result;
        public Object cookie;
    }

    protected class WorkerHandler extends Handler {
    	
        public WorkerHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
        	final DatabaseConnectivity connectivity = 
        		mDatabaseConnectivity;
        	
        	if (mDatabaseConnectivity == null) {
        		return;
        	}

            WorkerArgs args = (WorkerArgs) msg.obj;

            int token = msg.what;
            int event = msg.arg1;

            switch (event) {
                case EVENT_ARG_QUERY:
                    List<DatabaseObject> objects;
                    try {
                    	objects = 
                    		connectivity.query(args.query, args.projectionClass);
                    } catch (Exception e) {
                    	Logger.warnning("query failure: %s", e.toString());
                        objects = null;
                    }

                    args.result = objects;
                    break;

                case EVENT_ARG_QUERY_CURSOR:
                    Cursor cursor;
                    try {
                    	cursor = 
                    		connectivity.queryCursor(
                    				args.query, args.projectionClass);
                    } catch (Exception e) {
                    	Logger.warnning("query cursor failure: %s", e.toString());
                    	cursor = null;
                    }

                    args.result = cursor;
                    break;

                case EVENT_ARG_INSERT:
                	try {
                		args.result = connectivity.insert(args.objects[0]);
                    } catch (Exception e) {
                    	Logger.warnning("insert failure: %s", e.toString());
                    	args.result = 0;
                    }
                    break;

                case EVENT_ARG_BULK_INSERT:
                	try {
                		args.result = connectivity.insert(args.objects);
                    } catch (Exception e) {
                    	Logger.warnning("inserts failure: %s", e.toString());
                    	args.result = 0;
                    }
                    break;

               case EVENT_ARG_UPDATE:
                	try {
                		args.result = connectivity.update(args.query, 
                				args.objects[0]);
                    } catch (Exception e) {
                    	Logger.warnning("update failure: %s", e.toString());
                    	args.result = 0;
                    }
                    break;

                case EVENT_ARG_DELETE:
                	try {
                		args.result = connectivity.delete(args.query);
                    } catch (Exception e) {
                    	Logger.warnning("delete failure: %s", e.toString());
                    	args.result = 0;
                    }
                    break;
            }

            // passing the original token value back to the caller
            // on top of the event values in arg1.
            Message reply = args.handler.obtainMessage(token);
            reply.obj = args;
            reply.arg1 = msg.arg1;

            Logger.debug("WorkerHandler: event = %d, result = %s",
            		event,
            		args.result);

            reply.sendToTarget();
        }
    }
    
    protected class ResultHandler extends Handler {
    	
        public ResultHandler() {
        }

        public ResultHandler(Looper looper) {
            super(looper);
        }
        
        @SuppressWarnings("unchecked")
    	@Override
        public void handleMessage(Message msg) {
            Logger.debug("ResultHandler: msg = %s",
            		msg);

            WorkerArgs args = (WorkerArgs) msg.obj;

            int token = msg.what;
            int event = msg.arg1;

            // pass token back to caller on each callback.
            switch (event) {
                case EVENT_ARG_QUERY:
                    onQueryComplete(token, args.cookie, (List<DatabaseObject>) args.result);
                    break;

                case EVENT_ARG_QUERY_CURSOR:
                    onQueryCursorComplete(token, args.cookie, (Cursor) args.result);
                    break;

                case EVENT_ARG_INSERT:
                	Long rowId = (Long)args.result;
                    onInsertComplete(token, args.cookie, rowId.longValue());
                    break;

                case EVENT_ARG_BULK_INSERT:
                	Integer addRows = (Integer)args.result;
                    onBulkInsertComplete(token, args.cookie, addRows.intValue());
                    break;

                case EVENT_ARG_UPDATE:
                	Integer updateRows = (Integer)args.result;
                    onUpdateComplete(token, args.cookie, updateRows.intValue());
                    break;

                case EVENT_ARG_DELETE:
                	Integer deleteRows = (Integer)args.result;
                    onDeleteComplete(token, args.cookie, deleteRows.intValue());
                    break;
            }
        }

    }
    
    private static Looper sLooper = null;
    private static Looper sResultLooper = null;


    private Handler mWorkerThreadHandler;
    private Handler mResultThreadHandler;
    
    protected DatabaseConnectivity mDatabaseConnectivity;
    
	public AsyncDatabaseHelper(Context context, 
			Class<? extends DatabaseObject> objectClass) {
		this(context, null, objectClass);
	}
	
	public AsyncDatabaseHelper(Context context, 
			String authority,
			Class<? extends DatabaseObject> objectClass) {
		this(context, authority, objectClass, DatabaseObject.VERSION_LATEST);
	}
	
	public AsyncDatabaseHelper(Context context, 
			String authority,
			Class<? extends DatabaseObject> objectClass,
			boolean handleResultInThread) {
		this(context, authority, objectClass, 
				DatabaseObject.VERSION_LATEST, handleResultInThread);
	}
	
	public AsyncDatabaseHelper(Context context, 
			Class<? extends DatabaseObject> objectClass,
			int version) {
		this(context, null, objectClass, version);
	}
	
	public AsyncDatabaseHelper(Context context, 
			String authority,
			Class<? extends DatabaseObject> objectClass,
			int version) {
		this(context, authority, objectClass, version, false);
	}
	
	public AsyncDatabaseHelper(Context context, 
			String authority,
			Class<? extends DatabaseObject> objectClass,
			int version,
			boolean handleResultInThread) {
		mDatabaseConnectivity = 
			new DatabaseConnectivity(context, authority, objectClass, version);

        synchronized (AsyncDatabaseHelper.class) {
            if (sLooper == null) {
                HandlerThread thread = new HandlerThread("AsyncDatabaseWorker");
                thread.start();

                sLooper = thread.getLooper();
            }
        }
        
        mWorkerThreadHandler = createHandler(sLooper);
        
        if (handleResultInThread) {
            synchronized (AsyncDatabaseHelper.class) {
                if (sResultLooper == null) {
                    HandlerThread thread = new HandlerThread("AsyncDatabaseResult");
                    thread.start();

                    sResultLooper = thread.getLooper();
                }
            }
            
        	mResultThreadHandler = createResultHandler(sResultLooper);
        } else {
        	mResultThreadHandler = new ResultHandler();
        }
	}
	
    protected Handler createHandler(Looper looper) {
        return new WorkerHandler(looper);
    }

    protected Handler createResultHandler(Looper looper) {
        return new ResultHandler(looper);
    }

	public void startQueryCursor(int token, Object cookie, Query query) {
		startQueryCursor(token, cookie, query, null);
	}
	
	public void startQueryCursor(int token, Object cookie, 
    		Query query, Class<? extends DatabaseObject> projectClass) {
        // Use the token as what so cancelOperations works properly
		Message msg = mWorkerThreadHandler.obtainMessage(token);
        msg.arg1 = EVENT_ARG_QUERY;

        WorkerArgs args = new WorkerArgs();
        args.handler = mResultThreadHandler;
        args.query = query;
        args.projectionClass = projectClass;
        args.cookie = cookie;
        
        msg.obj = args;

        mWorkerThreadHandler.sendMessage(msg);
    }

	public void startQuery(int token, Object cookie, Query query) {
		startQuery(token, cookie, query, null);
	}
	
    public void startQuery(int token, Object cookie, 
    		Query query, Class<? extends DatabaseObject> projectClass) {
        // Use the token as what so cancelOperations works properly
        Message msg = mWorkerThreadHandler.obtainMessage(token);
        msg.arg1 = EVENT_ARG_QUERY;

        WorkerArgs args = new WorkerArgs();
        args.handler = mResultThreadHandler;
        args.query = query;
        args.projectionClass = projectClass;
        args.cookie = cookie;
        
        msg.obj = args;

        mWorkerThreadHandler.sendMessage(msg);
    }

    public final void cancelOperation(int token) {
        mWorkerThreadHandler.removeMessages(token);
    }

    public void startInsert(int token, Object cookie, 
    		DatabaseObject object) {
        // Use the token as what so cancelOperations works properly
        Message msg = mWorkerThreadHandler.obtainMessage(token);
        msg.arg1 = EVENT_ARG_INSERT;

        WorkerArgs args = new WorkerArgs();
        args.handler = mResultThreadHandler;
        args.objects = new DatabaseObject[] {object};
        args.cookie = cookie;
        
        msg.obj = args;

        mWorkerThreadHandler.sendMessage(msg);
    }

    public void startBulkInsert(int token, Object cookie, 
    		DatabaseObject[] objects) {
        // Use the token as what so cancelOperations works properly
        Message msg = mWorkerThreadHandler.obtainMessage(token);
        msg.arg1 = EVENT_ARG_BULK_INSERT;

        WorkerArgs args = new WorkerArgs();
        args.handler = mResultThreadHandler;
        args.objects = objects;
        args.cookie = cookie;
        
        msg.obj = args;

        mWorkerThreadHandler.sendMessage(msg);
    }

   public void startUpdate(int token, Object cookie, 
    		Query query, DatabaseObject object) {
        // Use the token as what so cancelOperations works properly
        Message msg = mWorkerThreadHandler.obtainMessage(token);
        msg.arg1 = EVENT_ARG_UPDATE;

        WorkerArgs args = new WorkerArgs();
        args.handler = mResultThreadHandler;
        args.query = query;
        args.objects = new DatabaseObject[] {object};
        args.cookie = cookie;
        
        msg.obj = args;

        mWorkerThreadHandler.sendMessage(msg);
    }

    public void startDelete(int token, Object cookie, 
    		Query query) {
        // Use the token as what so cancelOperations works properly
        Message msg = mWorkerThreadHandler.obtainMessage(token);
        msg.arg1 = EVENT_ARG_DELETE;

        WorkerArgs args = new WorkerArgs();
        args.handler = mResultThreadHandler;
        args.query = query;
        args.cookie = cookie;

        msg.obj = args;

        mWorkerThreadHandler.sendMessage(msg);
    }

    protected void onQueryComplete(int token, Object cookie, 
    		List<DatabaseObject> objects) {
    }

    protected void onQueryCursorComplete(int token, Object cookie, 
    		Cursor cursor) {
    }

    protected void onInsertComplete(int token, Object cookie, long rowId) {
    }

    protected void onBulkInsertComplete(int token, Object cookie, int rowsAdded) {
	}
    
    protected void onUpdateComplete(int token, Object cookie, int rowsUpdated) {
    }

    protected void onDeleteComplete(int token, Object cookie, int rowsDeleted) {
    }

}
