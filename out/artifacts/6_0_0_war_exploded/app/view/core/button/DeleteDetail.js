/**
 * 明细行删除按钮
 */	
Ext.define('erp.view.core.button.DeleteDetail',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpDeleteDetailButton',
		iconCls: 'x-button-icon-detailclose',
    	cls: 'x-btn-tb',
    	tooltip: $I18N.common.button.erpDeleteDetailButton,
    	disabled: true,
        //width: 90,
		initComponent : function(){ 
			this.callParent(arguments); 
			this.addEvents({//添加[删除明细后]事件
				beforedelete: true,
				afterdelete: true
			});
		},
		canDelete:true,
		handler: function(btn){
			var me = this;
			var grid = btn.grid ||btn.ownerCt.ownerCt;
			var callerConfig = (grid.caller&&grid.caller!='')?grid.caller:caller;
			//解决针对两个从表无法控制多个从表的权限
			var records = grid.selModel.getSelection();
			if(records.length > 0){
				if(grid.keyField){
					me.fireEvent('beforedelete', records[0].data, records[0], me);
					if(me.canDelete){
					if(records[0].data[grid.keyField] != null && records[0].data[grid.keyField] > 0){
						warnMsg($I18N.common.msg.ask_del, function(btn){
							if(btn == 'yes'){
								var url = "common/deleteDetail.action?_noc=" + (grid._noc || 0);
								grid.setLoading(true);//loading...
								Ext.Ajax.request({
							   		url : basePath + url,
							   		params: {
							   			caller:caller,
							   			gridcaller: callerConfig,
							   			condition: grid.keyField + "=" + records[0].data[grid.keyField],
							   			gridReadOnly:grid.readOnly
							   		},
							   		method : 'post',
							   		callback : function(options,success,response){
							   			grid.setLoading(false);
							   			var localJson = new Ext.decode(response.responseText);
							   			if(localJson.exceptionInfo){
						        			showError(localJson.exceptionInfo);return;
						        		}
						    			if(localJson.success){
						    				me.fireEvent('afterdelete', records[0].data, records[0], me);//触发删除后事件
						    				grid.store.remove(records[0]);
							   				delSuccess(function(){
									   										
											});//@i18n/i18n.js
							   			} else {
							   				delFailure();
							   			}
							   		}
								});
							}
						});
					} else {
						me.fireEvent('afterdelete', records[0].data, records[0], me);
						grid.store.remove(records[0]);
					}
					}
				} else {
					if(records[0].data[grid.keyField] != null && records[0].data[grid.keyField] > 0){
						showError("grid未配置keyField，无法删除该行数据!");
					} else {
						grid.store.remove(records[0]);
					}
				}
			}
		},
		autoSetSequence: false,//删除明细行后,自动调整detno排序字段
		listeners: {
			afterdelete: function(d, r, btn){
				var grid = btn.ownerCt.ownerCt;
				if(btn.autoSetSequence || grid.autoSetSequence) {
					btn.setSequence(grid, r);
				}
			}
		},
		setSequence: function(grid, record){
			if(grid.detno) {
				if(grid.keyField && record.data[grid.keyField] != null && record.data[grid.keyField] > 0){
					if(grid.mainField) {
						Ext.Ajax.request({
							url: basePath + 'common/setDetailDetno.action',
							params: {
								caller: caller,
								dfield: grid.detno,
								mfield: grid.mainField,
								id: record.data[grid.mainField],
								detno: record.data[grid.detno]
							},
							callback: function(opt, s, r){
								var res = Ext.decode(r.responseText);
								if(res.exceptionInfo) {
									showError(res.exceptionInfo);
								}
							}
						});
					}
				}
				var items = grid.store.data.items;
				Ext.each(items, function(item){
					if(item.index != record.index && item.data[grid.detno] > record.data[grid.detno]) {
						item.set(grid.detno, item.data[grid.detno] - 1);
					}
				});
			}
		}
	});