/**
 * 产品项目绑定物料按钮
 */	
Ext.define('erp.view.core.button.ProductBound',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpProductBoundButton',
//		text: $I18N.common.button.erpVastTurnPurcButton,
    	iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	width: 50,
    	id: 'erpProductBoundButton',
		initComponent : function(){ 
			this.callParent(arguments); 
		},
//		handler: function(btn){
//			var records = this.getSelectedRecords();
//			if(records.length > 0){
//				var pu = Ext.getCmp('pu_code');
//				if(pu.value == null || pu.value == ''){//未指定采购单
//					var ve = Ext.getCmp('ap_vendcode');
//					if(ve.value == null || ve.value == ''){
//						var bool = true;
//						Ext.each(records, function(r, index){
//							if(r.data['ad_vendor'] == null || r.data['ad_vendor'] == ''){
//								bool = false;
//							}
//						});
//						if(!bool){
//							showError("有未选择供应商的明细行，请先获取供应商，或指定到采购单、供应商");return;
//						}
//					}
//				}
//			}
//			this.save(btn.ownerCt.ownerCt.dealUrl);
//		},
//		getSelectedRecords: function(){
//	    	var grid = Ext.getCmp('batchDealGridPanel');
//	        var items = grid.selModel.getSelection();
//	        Ext.each(items, function(item, index){
//	        	if(this.data[grid.keyField] != null && this.data[grid.keyField] != ''
//	        		&& this.data[grid.keyField] != '0' && this.data[grid.keyField] != 0){
//	        		var bool = true;
//	        		Ext.each(grid.multiselected, function(){
//	        			if(this.data[grid.keyField] == item.data[grid.keyField]){
//	        				bool = false;
//	        			}
//	        		});
//	        		if(bool){
//	        			grid.multiselected.push(item);
//	        		}
//	        	}
//	        });
//			return grid.multiselected;
//		},
//		save: function(url){
//	    	var grid = Ext.getCmp('batchDealGridPanel');
//	        var items = grid.selModel.getSelection();
//	        Ext.each(items, function(item, index){
//	        	if(this.data[grid.keyField] != null && this.data[grid.keyField] != ''
//	        		&& this.data[grid.keyField] != '0' && this.data[grid.keyField] != 0){
//	        		var bool = true;
//	        		if(bool){
//	        			grid.multiselected.push(item);
//	        		}
//	        	}
//	        });
//	    	var form = Ext.getCmp('dealform');
//			var records = Ext.Array.unique(grid.multiselected);
//			if(records.length > 0){
//				var params = new Object();
//				params.caller = caller;
//				var data = new Array();
//				var bool = false;
//				Ext.each(records, function(record, index){
//					var f = form.fo_detailMainKeyField;
//					if((grid.keyField && this.data[grid.keyField] != null && this.data[grid.keyField] != ''
//		        		&& this.data[grid.keyField] != '0' && this.data[grid.keyField] != 0) 
//		        		||(f && this.data[f] != null && this.data[f] != ''
//			        		&& this.data[f] != '0' && this.data[f] != 0)){
//						bool = true;
//						var o = new Object();
//						if(grid.keyField){
//							o[grid.keyField] = record.data[grid.keyField];
//						} else {
//							params.id[index] = record.data[form.fo_detailMainKeyField];
//						}
//						if(grid.toField){
//							Ext.each(grid.toField, function(f, index){
//								var v = Ext.getCmp(f).value;
//								if(v != null && v.toString().trim() != '' && v.toString().trim() != 'null'){
//									o[f] = v;
//								}
//							});
//						}
//						if(grid.necessaryFields){
//							Ext.each(grid.necessaryFields, function(f, index){
//								o[f] = record.data[f];
//							});
//						}
//						data.push(o);
//					}
//				});
//				if(bool){
//					params.data = Ext.encode(data);
//					var main = parent.Ext.getCmp("content-panel");
//					main.getActiveTab().setLoading(true);//loading...
//					Ext.Ajax.request({
//				   		url : basePath + url,
//				   		params: params,
//				   		method : 'post',
//				   		callback : function(options,success,response){
//				   			main.getActiveTab().setLoading(false);
//				   			var localJson = new Ext.decode(response.responseText);
//				   			if(localJson.exceptionInfo){
//				   				showError(localJson.exceptionInfo);
//				   				return;
//				   			}
//			    			if(localJson.success){
//			    				if(localJson.log){
//			    					showMessage("提示", localJson.log, 15000);
//			    				}
//				   				Ext.Msg.alert("提示", "处理成功!", function(){
//				   					grid.multiselected = new Array();
//				   					Ext.getCmp('dealform').onQuery();
//				   				});
//				   			}
//				   		}
//					});
//				} else {
//					showError("没有需要处理的数据!");
//				}
//			}
//		}
	});