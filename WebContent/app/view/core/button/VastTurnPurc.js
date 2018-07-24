/**
 * 批量转采购单按钮
 */	
Ext.define('erp.view.core.button.VastTurnPurc',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpVastTurnPurcButton',
		text: $I18N.common.button.erpVastTurnPurcButton,
    	iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	width: 110,
    	id: 'erpVastTurnPurcButton',
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		handler: function(btn){
			var records = this.getSelectedRecords();
			if(records.length > 0){
				var pu = Ext.getCmp('pu_code');
				if(pu.value == null || pu.value == ''){//未指定采购单
					var ve = Ext.getCmp('ap_vendcode');
					if(ve.value == null || ve.value == ''){
						var bool = true;
						Ext.each(records, function(r, index){
							if(r.data['ad_vendor'] == null || r.data['ad_vendor'] == ''){
								bool = false;
							}
						});
						if(!bool){
							showError("有未选择供应商的明细行，请先获取供应商，或指定到采购单、供应商");return;
						}
					}
				}
			}
			this.save(btn.ownerCt.ownerCt.dealUrl);
		},
		getSelectedRecords: function(){
	    	var grid = Ext.getCmp('batchDealGridPanel');
	    	var checkdata=[];
	    	grid.multiselected = [];
	    	Ext.each(grid.tempStore,function(d){//转存区数据
	    		var keys=Ext.Object.getKeys(d);
				Ext.each(keys, function(k){
					checkdata.push(d[k]);
				});
	    	});
	        var items = grid.selModel.getSelection();
	        if(checkdata.length>0&&items.length>0){
	        	showError('暂存区已经有数据，当前筛选界面勾选的数据无效，请取消勾选或添加到暂存区');
	        	return false;
	        }else if(items.length>0){
		        Ext.each(items, function(item, index){
		        	if(this.data[grid.keyField] != null && this.data[grid.keyField] != ''
		        		&& this.data[grid.keyField] != '0' && this.data[grid.keyField] != 0){
		        		var bool = true;
		        		if(!this.data['ad_tqty']){
	        				bool = false;
	        			}
		        		Ext.each(grid.multiselected, function(){
		        			if(this.data[grid.keyField] == item.data[grid.keyField]){
		        				bool = false;
		        			}
		        		});
		        		if(bool){
		        			grid.multiselected.push(item);
		        		}
		        	}
		        });
		    }else{
	        	grid.multiselected=checkdata;
	        }
	        return grid.multiselected;
		},
		save: function(url){
	    	var grid = Ext.getCmp('batchDealGridPanel');
	    	var checkdata=[];
	    	Ext.each(grid.tempStore,function(d){//转存区数据
	    		var keys=Ext.Object.getKeys(d);
				Ext.each(keys, function(k){
					checkdata.push(d[k]);
				});
	    	});
	        var items = grid.selModel.getSelection();
	        if(checkdata.length>0&&items.length>0){
	        	showError('暂存区已经有数据，当前筛选界面勾选的数据无效，请取消勾选或添加到暂存区');
	        	return;
	        }else if(items.length>0){
		        Ext.each(items, function(item, index){
		        	if(this.data[grid.keyField] != null && this.data[grid.keyField] != ''
		        		&& this.data[grid.keyField] != '0' && this.data[grid.keyField] != 0){
		        		var bool = true;
		        		if(!this.data['ad_tqty']){
	        				bool = false;
	        			}
		        		Ext.each(grid.multiselected, function(){
		        			if(this.data[grid.keyField] == item.data[grid.keyField]){
		        				bool = false;
		        			}
		        		});
		        		if(bool){
		        			grid.multiselected.push(item);
		        		}
		        	}
		        });
	        }else{
	        	grid.multiselected=checkdata;
	        }
	    	var form = Ext.getCmp('dealform');
			var records = Ext.Array.unique(grid.multiselected);
			if(records.length > 0){
				var params = new Object();
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
								o[f] = record.data[f];
							});
						}
						data.push(o);
					}
				});
				if(bool){
					params.data = Ext.encode(data);
					var main = parent.Ext.getCmp("content-panel");
					if(!main){
						main = parent.parent.Ext.getCmp("content-panel");
					}
					main.getActiveTab().setLoading(true);//loading...
					Ext.Ajax.request({
				   		url : basePath + url,
				   		params: params,
				   		method : 'post',
				   		callback : function(options,success,response){
				   			main.getActiveTab().setLoading(false);
				   			var localJson = new Ext.decode(response.responseText);
				   			if(localJson.exceptionInfo){
				   				showError(localJson.exceptionInfo);
				   				return;
				   			}
			    			if(localJson.success){
			    				if(localJson.log){
			    					showMessage("提示", localJson.log, 15000);
			    				}
			    				
				   				Ext.Msg.alert("提示", "处理成功!"/*, function(){
				   					
				   				}*/);
				   				grid.tempStore={};//操作成功后清空暂存区数据
				   				grid.multiselected = new Array();
				   				Ext.getCmp('dealform').onQuery();
				   			}
				   		}
					});
				} else {
					showError("没有需要处理的数据!");
				}
			} else {
				showError("请勾选需要的明细!");
			}
		}
	});