/**
 * 取消采购通知
 */	
Ext.define('erp.view.core.button.PurchaseNotifyCancel',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpPurchaseNotifyCancelButton',
		iconCls: 'x-button-icon-delete',
    	cls: 'x-btn-gray',
    	text: '取消通知',
    	id: 'cancelm',
    	style: {
    		marginLeft: '10px'
        },
        width: 120,
        menu: [{
			iconCls: 'main-msg',
	        text: '取消通知',
	        listeners: {
	        	click: function(m){ 
	        		var params=new Object();
	        		var cancel=Ext.getCmp('cancelm');  
	        		params.data = unescape(Ext.JSON.encode(cancel.getRecordDatas()).replace(/\\/g,"%"));
	        		var records = Ext.Array.unique(Ext.getCmp('batchDealGridPanel').multiselected); //2018060713  提醒勾选了多少条  maz 
	        		if(records == 0 ){
	        			showError('请先勾选需要取消提醒的数据');
	        			return;
	        		}
	        		warnMsg("勾选了"+records.length+"行,确定取消通知已勾选的提醒吗？", function(b){
    					if(b == 'yes'){
    						cancel.Cancle(params,'scm/cancelPurchaseNotify.action');
    					}
    				});
	        	}
	        }
	    },{ 
	    	iconCls: 'main-msg',
	        text: '取消当前结果通知',
	        listeners: {
	        	click: function(m){
	        		var params=new Object();
	        		var form = Ext.getCmp('dealform');
	        		var cancel=Ext.getCmp('cancelm');
	        		params.condition = form.getCondition(); 
	        		warnMsg("将会取消所有满足主表条件的提醒，确定取消吗？", function(b){
    					if(b == 'yes'){
    						cancel.Cancle(params,'scm/cancelCondPurcNotify.action');
    					}
    				});
	        	}
	        }
	    },{
	    	iconCls:'main-msg',
	        text:'取消未确认通知',
	        listeners: {
	        	click: function(m){
	        		var params=new Object();
	        		var cancel=Ext.getCmp('cancelm');
	        		params.condition="ALL";
	        		warnMsg("将会取消所有未确认的提醒，确定取消吗？", function(b){
    					if(b == 'yes'){
    						cancel.Cancle(params,'scm/cancelALLPurcNotify.action');
    					}
    				});
	        	}
	        }
	    }],
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		Cancle:function(param,acurl){
	    	var main = parent.Ext.getCmp("content-panel");
	    	var grid = Ext.getCmp('batchDealGridPanel');
			main.getActiveTab().setLoading(true);//loading...
			Ext.Ajax.request({
				url : basePath + acurl,
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
	    }
	});