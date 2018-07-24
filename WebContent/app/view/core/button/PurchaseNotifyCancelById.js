/**
 * 取消采购通知
 */	
Ext.define('erp.view.core.button.PurchaseNotifyCancelById',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpPurchaseNotifyCancelByIdButton',
		iconCls: 'x-button-icon-delete',
    	cls: 'x-btn-gray',
    	text: '取消通知',
    	style: {
    		marginLeft: '10px'
        },
        width: 120,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		handler: function(){
			var me=this;
			var params=new Object();  
	        params.data = unescape(Ext.JSON.encode(me.getRecordDatas()).replace(/\\/g,"%"));
	    	var main = parent.Ext.getCmp("content-panel");
	    	var grid = Ext.getCmp('batchDealGridPanel');
			main.getActiveTab().setLoading(true);//loading...
			Ext.Ajax.request({
				url : basePath + 'scm/cancelPurchaseNotify.action',
		   		params: params,
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