/**
 * 设置供应商比例分配表
 */	
Ext.define('erp.view.core.button.SetVendorRateAdd',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpSetVendorRateAddButton',
		iconCls: 'x-button-icon-delete',
    	cls: 'x-btn-gray',
    	text: '维护分配',
    	id: 'SetVRA',
    	style: {
    		marginLeft: '10px'
        },
        width: 120,      
        listeners:{
        	click:function(btn){
            var items =Ext.getCmp('batchDealGridPanel').selModel.getSelection()[0];
            if(items){
            	 var formCondition="pr_id IS"+items.data.pr_id;
            	 var gridCondition="pr_id IS"+items.data.pr_id;
        	 var win = new Ext.window.Window(
 							{  
 								id : 'win',
 								height : '90%',
 								width : '80%',
 								maximizable : true,
 								buttonAlign : 'center',
 								layout : 'anchor',
 								items : [ {
 									tag : 'iframe',
 									frame : true,
 									anchor : '100% 100%',
 									layout : 'fit',
 								    html : '<iframe id="iframe_'+"SetProductVendorRate"+'" src="'+basePath+'jsps/scm/purchase/productVendorRate.jsp?whoami='+"SetProductVendorRate"+'&formCondition='+formCondition+'&gridCondition='+gridCondition+'" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
 								} ]
                               /* listeners:{
                                  'beforeclose':function(view ,opt){
                                	var grid = Ext.getCmp('grid');
	     			        		var value = Ext.getCmp('ma_id').value;
	     			        		var gridCondition = grid.mainField + '=' + value,
	     			        		gridParam = {caller: caller, condition: gridCondition};
	     			        		me.GridUtil.loadNewStore(grid, gridParam);  
                                  }	
                                	
                                }*/
 							});
            }else {
            	 var win = new Ext.window.Window(
 							{  
 								id : 'win',
 								height : '90%',
 								width : '80%',
 								maximizable : true,
 								buttonAlign : 'center',
 								layout : 'anchor',
 								items : [ {
 									tag : 'iframe',
 									frame : true,
 									anchor : '100% 100%',
 									layout : 'fit',
 								    html : '<iframe id="iframe_'+"SetProductVendorRate"+'" src="'+basePath+'jsps/scm/purchase/productVendorRate.jsp?whoami='+"SetProductVendorRate"+'" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
 								} ]             
 							});          	
           	
               }
 					win.show();
        	}
        },
   /*     menu: [{
			iconCls: 'main-msg',
	        text: '重新设置',
	        listeners: {
	        	click: function(m){
	        		var params=new Object(); 
	        		params.Mode="重新设置";
	        		params.SetEndDate=Ext.getCmp('pv_enddate').value;
	        		params.Kind2=Ext.getCmp('rate2').value;
	        		params.Kind3=Ext.getCmp('rate3').value;
	        		if (params.SetEndDate==null ||params.SetEndDate=='' ){
	        			showError("请录入有效期 ，作为新设比例的有效截止日期!");
	        			return;
	        		}
	        		if (params.Kind2==null ||params.Kind2=='' ){
	        			showError("请录入二分原则!");
	        			return;
	        		}
	        		Ext.getCmp('SetVR').SetRate(params);
	        	}
	        }
	    },{ 
	    	iconCls: 'main-msg',
	        text: '追加新料料供应商报价',
	        listeners: {
	        	click: function(m){
	        		var params=new Object(); 
	        		params.Mode="追加新料";
	        		params.SetEndDate=Ext.getCmp('pv_enddate').value;
	        		params.Kind2=Ext.getCmp('rate2').value;
	        		params.Kind3=Ext.getCmp('rate3').value;
	        		Ext.getCmp('SetVR').SetRate(params);
	        	}
	        }
	    },{
	       iconCls:'main-msg',
	       text:'失效价格重设',
	       listeners: {
	        	click: function(m){
	        		var params=new Object(); 
	        		params.Mode="失效价格重设";
	        		params.SetEndDate=Ext.getCmp('pv_enddate').value;
	        		params.Kind2=Ext.getCmp('rate2').value;
	        		params.Kind3=Ext.getCmp('rate3').value;
	        		Ext.getCmp('SetVR').SetRate(params);
	        	}
	        }
	    }],
	    SetRate:function(param){
	    	var main = parent.Ext.getCmp("content-panel");
			main.getActiveTab().setLoading(true);//loading...
			Ext.Ajax.request({
		   		url : basePath +'scm/setVendorRate.action',
		   		params: param,
		   		method : 'post',
		   		timeout: 6000000,
		   		callback : function(options,success,response){
		   			main.getActiveTab().setLoading(false);
		   			var localJson = new Ext.decode(response.responseText);
		   			if(localJson.exceptionInfo){
		   				var str = localJson.exceptionInfo;
		   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){
		   					str = str.replace('AFTERSUCCESS', '');
		   					grid.multiselected = new Array();
		   					Ext.getCmp('dealform').onQuery();
		   				}
		   				showError(str);return;
		   			}
	    			if(localJson.success){
	    				if(localJson.log){
	    					showMessage("提示", localJson.log);
	    				}
		   				Ext.Msg.alert("提示", "处理成功!", function(){
		   					grid.multiselected = new Array();
		   					Ext.getCmp('dealform').onQuery();
		   				});
		   			}
		   		}
			});
	    },
	    getRecordDatas:function(){
	    	var grid = Ext.getCmp('batchDealGridPanel');
	        var items = grid.selModel.getSelection();
	        Ext.each(items, function(item, index){
	        	if(this.data[grid.keyField] != null && this.data[grid.keyField] != ''
	        		&& this.data[grid.keyField] != '0' && this.data[grid.keyField] != 0){
	        		item.index = this.data[grid.keyField];
	        		grid.multiselected.push(item);
	        	}
	        });
	    	var form = Ext.getCmp('dealform');
			var records = Ext.Array.unique(grid.multiselected);
				var data = new Array();
				Ext.each(records, function(record, index){
					var f = form.fo_detailMainKeyField;
					if((grid.keyField && this.data[grid.keyField] != null && this.data[grid.keyField] != ''
		        		&& this.data[grid.keyField] != '0' && this.data[grid.keyField] != 0) 
		        		||(f && this.data[f] != null && this.data[f] != ''
			        		&& this.data[f] != '0' && this.data[f] != 0)){
						bool = true;
						var o = new Object();
						if(grid.keyField){
							o[grid.keyField] = record.data[grid.keyField];
						} else {
							params.id[index] = record.data[form.fo_detailMainKeyField];
						}
						if(grid.toField){
							Ext.each(grid.toField, function(f, index){
								var v = Ext.getCmp(f).value;
								if(v != null && v.toString().trim() != '' && v.toString().trim() != 'null'){
									o[f] = v;
								}
							});
						}
						if(grid.necessaryFields){
							Ext.each(grid.necessaryFields, function(f, index){
								var v = record.data[f];
								if(Ext.isDate(v)){
									v = Ext.Date.toString(v);
								}
								o[f] = v;
							});
						}
						data.push(o);
					}
				});
	    	return data;
	    },*/
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});