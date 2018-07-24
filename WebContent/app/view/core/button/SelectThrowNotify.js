Ext.define('erp.view.core.button.SelectThrowNotify',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpSelectThrowNotifyButton',
		iconCls: 'x-button-icon-save',
    	cls: 'x-btn-gray-1',
    	text: $I18N.common.button.erpSelectThrowButton,
    	style: {
    		marginLeft: '10px'
        },
        width:90,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		handler:function(btn){
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
			if(records.length > 0){
				var params = new Object();
				params.id=new Array();
				params.caller = caller;
				var data = new Array();
				var bool = false;
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
				if(bool){
					params.data = unescape(Ext.JSON.encode(data).replace(/\\/g,"%"));;
					var main = parent.Ext.getCmp("content-panel");
					main.getActiveTab().setLoading(true);//loading...
					Ext.Ajax.request({
				   		url : basePath + 'pm/wcplan/throwpurchasenotify.action',
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
				} else {
					showError("没有需要处理的数据!");
				}
			}
		}
	});