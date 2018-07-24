Ext.define('erp.view.core.button.FlowChangeHandler',{ 
	extend: 'Ext.Button', 
	alias: 'widget.erpFlowChangeHandlerButton',
	text: $I18N.common.button.erpFlowChangeHandlerButton,
	iconCls: 'x-button-icon-scan',
	cls: 'x-btn-gray',
	margin:'0 5 0 0',
	id: 'erpFlowChangeHandlerButton',
	initComponent : function(){ 
		this.callParent(arguments); 
	},
	handler:function(){
		var form = Ext.getCmp('dealform');
		var grid = Ext.getCmp('batchDealGridPanel');
    	var count=0;
    	grid.getMultiSelected();
    	if(grid.multiselected.length>0){
    		var items = grid.selModel.getSelection();
            Ext.each(items, function(item, index){
            	//不能选择END流程变更
            	if(item.data.fi_nodename=='END'){
            		count++;
            	}
            });
    	}
    	if(count>0){
    		showError("不能选择已结束的流程变更责任人");
    		return false;
    	}
		var records = Ext.Array.unique(grid.multiselected);
		if(records.length>0){
			var win =new Ext.window.Window({
				title: '<span style="color:#115fd8;">选择责任人</span>',
				draggable:true,
				height: '30%',
				width: '30%',
				resizable:false,
				id:'FlowChangeWin',
				cls:'FlowChangeWin',
				iconCls:'x-button-icon-set',
		   		modal: true,
		   		bbar:['->',{
		   			cls:'x-btn-gray',
		   			xtype:'button',
		   			text:'确认',
		   			handler:function(btn){
		   				var db = Ext.getCmp('changeHandler');
		   				if(!db.value){
		   					Ext.Msg.alert('提示','请选择人员再进行确认操作');
		   					return;
		   				}
		   				//收集单据id信息
		   				ids = [];
						Ext.each(records, function(record, index){
				        	ids.push({
				            	id:record.data.fi_keyvalue
				        	});
					 	});
						 warnMsg('确定要变更所选流程的责任人吗？', function(btn){
						 	if(btn == 'yes'){
								form.setLoading(true);
								Ext.Ajax.request({
									url : basePath + 'oa/flow/updateHandler.action',
									params: {
										ids:Ext.JSON.encode(ids),
										code:db.value
									},
									method : 'post',
									callback : function(options,success,response){
										form.setLoading(false);
										var localJson = new Ext.decode(response.responseText);
										if(localJson.exceptionInfo){
											showError(localJson.exceptionInfo);return;
										}
										if(localJson.success){
											showInformation('更新责任人成功！', function(btn){
												Ext.getCmp('dealform').onQuery();
												var win = Ext.getCmp('FlowChangeWin');
		   										win.close();
											});
										} else {
											delFailure();
										}
									}
								});
							}
						 });
		   			}
		   		},{xtype:'splitter',width:10},{
		   			cls:'x-btn-gray',
		   			xtype:'button',
		   			text:'取消',
		   			handler:function(btn){
		   				var win = Ext.getCmp('FlowChangeWin');
		   				win.close();
		   			}
		   		},'->'],
			   	items: [{
			   		id:'changeHandler',
			   		allowBlank:false,
					allowDecimals:true,
					checked:false,
					padding:'10 0 0 0',
					fieldLabel:"责任人编号",
					fieldStyle:"background:#fff;",
					hideTrigger:false,
					labelAlign:"left",
					labelStyle:"color:black",							
					maxLength:50,
					maxLengthText:"字段长度不能超过50字符!",
					name:"changeHandler",
					readOnly:false,
					table:"CUSTOMTABLE",
					xtype:"dbfindtrigger"
	    		}]
			});
			win.show();	
		}else{
			showError("请勾选数据!");
		}
	}
});