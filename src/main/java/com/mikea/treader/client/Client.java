package com.mikea.treader.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Client implements EntryPoint {
  private final ClientServiceAsync clientService = GWT.create(ClientService.class);

  public void onModuleLoad() {
      clientService.getMessage(new AsyncCallback<String>() {
          @Override
          public void onFailure(Throwable caught) {
              Window.alert(caught.toString());
          }

          @Override
          public void onSuccess(String result) {
              Window.alert(result);
          }
      });
  }
}
