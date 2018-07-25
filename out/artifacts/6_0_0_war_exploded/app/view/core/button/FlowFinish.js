/**
 * 流程结束按钮
 */	
Ext.define('erp.view.core.button.FlowFinish',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpFlowFinishButton',
		iconCls: 'x-button-icon-scan',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpFlowFinishButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 90,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		handler: function() {
	    	var startdealtime = new Date(),grid = Ext.getCmp('batchDealGridPanel');
	    	var resc = grid.selModel.getSelection();
	    	if(resc.length !=0 ){
	    		if(resc.length!=1){
	    			Ext.Msg.alert('提示', "只能进行单一流程结束!");
	    		}else{
	        		var jp_processInstanceId = resc[0].get('jp_processinstanceid');
	        		var taskId = resc[0].get('jp_nodeid');
	        		if(taskId!=null&&jp_processInstanceId!=null){
	        			Ext.Ajax.request({
	            			url: basePath + 'common/endProcessInstance.action',
	            			params: {
	            				processInstanceId: jp_processInstanceId,
	            				holdtime: ((startdealtime - basestarttime) / 1000).toFixed(0),
	            				nodeId: taskId
	            			},
	            			callback: function(options, success, response) {
	            				var text = response.responseText;
	            				jsonData = Ext.decode(text);
	            				if (jsonData.success) {
	            					Ext.Msg.alert('提示', "流程已结束!");	
	            					Ext.getCmp('dealform').onQuery();
	            				}
	            				if (jsonData.exceptionInfo) {
	            					showError(jsonData.exceptionInfo);
	            					Ext.Msg.alert('提示', "该流程实例不存在!");
	            				}
	            			}
	            		});
	        		}else{
	        			showError("请配置form从表字段  实例版本号与状态!");
	        		}
	    		}
	    	}else{
	    		showError("请勾选需要的明细!");
	    	}
	    	
		}
	});