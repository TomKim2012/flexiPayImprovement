package com.workpoint.icpak.client.service;

import com.gwtplatform.dispatch.rpc.shared.Result;

/**
 * 
 * @author duggan
 *
 * @param <T>
 */
public abstract class TaskServiceCallback<T extends Result> extends ServiceCallback<T> {

	@Override
	public void onSuccess(T aResponse) {
//		BaseResponse baseResult = (BaseResponse)aResponse;
//		
//		if(baseResult.getErrorCode()==0){
//			processResult(aResponse);
//		}else{
//			//throw error
//			AppContext.getEventBus().fireEvent(new ProcessingCompletedEvent());
//			AppContext.getEventBus().fireEvent(new ErrorEvent(baseResult.getErrorMessage(), baseResult.getErrorId()));
//		}
			
	}
}
