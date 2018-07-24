Ext.define('erp.view.plm.test.CheckListBase',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit',
	id:'CheckListBase', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 30%',
					saveUrl: 'plm/check/saveCheckListBase.action',
					deleteUrl: 'plm/check/deleteCheckListBase.action',
					updateUrl: 'plm/check/updateCheckListBase.action',
					endUrl: '/plm/test/EndProject.action',
					resAuditUrl: 'plm/check/resAuditCheckListBase.action',
					auditUrl: 'plm/check/auditCheckListBase.action',
					submitUrl: 'plm/check/submitCheckListBase.action',
					resSubmitUrl: 'plm/check/resSubmitCheckListBase.action',
					getIdUrl: 'common/getId.action?seq=CheckListBase_SEQ',
					keyField: 'cb_id',
					codeField:'cb_code'
				},
				{
					xtype: 'erpGridPanel2',
					anchor: '100% 70%',
					detno: 'cbd_detno',	
					keyField:'cbd_id',
					mainField: 'cbd_cbid',
					multiselected:new Array(),
					allowExtraButtons:true,
					listeners: {
		    			scrollershow: function(scroller) {
		    				if (scroller && scroller.scrollEl) {
		    					scroller.clearManagedListeners();  
		    					scroller.mon(scroller.scrollEl, 'scroll', scroller.onElScroll, scroller);  
		    				}
		    			}
		    		},
					plugins: [Ext.create('erp.view.core.grid.HeaderFilter', {
						remoteFilter: true
					}),
					Ext.create('Ext.grid.plugin.CellEditing', {
						clicksToEdit: 1,
						listeners:{/*
							'edit':function(editor,e,Opts){
								var record=e.record;
								if(e.originalValue!=e.value){
									Ext.MessageBox.show({
										title:'保存修改?',
										msg: '数据已修改需要保存吗？',
										buttons: Ext.Msg.YESNO,
										icon: Ext.Msg.WARNING,
										fn: function(btn){
											if(btn == 'yes'){ 				    	
												var grid=Ext.getCmp('grid');
												Ext.Ajax.request({
													url:basePath+'plm/test/updateResult.action',
													params: {
														data:e.value,
														field:e.field,
														keyValue:record.data[grid.keyField]
													},
													method : 'post',
													callback : function(options,success,response){
														var local=Ext.decode(response.responseText);
														if(local.success) {
															grid.GridUtil.loadNewStore(grid,{
																caller:caller,
																condition:grid.mainField+"="+record.data[grid.mainField]
															})
															showMessage('提示', '保存成功!', 1000);
														}else {
															showError(local.exceptionInfo);
														}
													}
												});
											} else if(btn == 'no'){
												//不保存	
												e.record.reject();
											} else {
												return;
											}
										}
									});
								}
							}
						*/}
					}), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
					selModel: Ext.create('Ext.selection.CheckboxModel',{
						ignoreRightMouseSelection : false,
						listeners:{
							selectionchange:function(selectionModel, selected, options){

							}
						},
						getEditor: function(){
							return null;
						},
						onRowMouseDown: function(view, record, item, index, e) {//改写的onRowMouseDown方法
							var me = Ext.getCmp('grid');
							var bool = true;
							var items = me.selModel.getSelection();
							Ext.each(items, function(item, index){
								if(item && record && item.data.cbd_id == record.data.cbd_id){
									bool = false;
									me.selModel.deselect(record);
									Ext.Array.remove(items, item);
									Ext.Array.remove(me.multiselected, record);
								}
							});
							Ext.each(me.multiselected, function(item, index){
								items.push(item);
							});
							if(bool){
								view.el.focus();
								var checkbox = item.childNodes[0].childNodes[0].childNodes[0];
								if(checkbox.getAttribute && checkbox.getAttribute('class') == 'x-grid-row-checker'){
									me.multiselected.push(record);
									items.push(record);
									me.selModel.select(me.multiselected);
								} else {
									me.selModel.deselect(record);
									Ext.Array.remove(me.multiselected, record);
								}
							}
							if(items.length>0){						        	  
								Ext.getCmp('set').setDisabled(false);
								Ext.getCmp('set1').setDisabled(false);
								Ext.getCmp('set2').setDisabled(false);
								var btn = Ext.getCmp('updatetestresult');
								if(btn){
									btn.setDisabled(false);
								}
							}else {
								Ext.getCmp('set').setDisabled(true);
								Ext.getCmp('set1').setDisabled(true);
								Ext.getCmp('set2').setDisabled(true);
								var btn = Ext.getCmp('updatetestresult');
								if(btn){
									btn.setDisabled(true);
								}
							}
						},
						onHeaderClick: function(headerCt, header, e) {
							if (header.isCheckerHd) {
								e.stopEvent();
								var isChecked = header.el.hasCls(Ext.baseCSSPrefix + 'grid-hd-checker-on');
								if (isChecked) {
									this.deselectAll(true);
									var grid = Ext.getCmp('grid');
									this.deselect(grid.multiselected);
									grid.multiselected = new Array();
									var els = Ext.select('div[@class=x-grid-row-checker-checked]').elements;
									Ext.each(els, function(el, index){
										el.setAttribute('class','x-grid-row-checker');
									});
									Ext.getCmp('set').setDisabled(true);
									Ext.getCmp('set1').setDisabled(true);
									Ext.getCmp('set2').setDisabled(true);
									header.el.removeCls(Ext.baseCSSPrefix + 'grid-hd-checker-on');//添加这个
								} else {
									header.el.addCls(Ext.baseCSSPrefix + 'grid-hd-checker-on');//添加这个
									var grid = Ext.getCmp('grid');
									this.deselect(grid.multiselected);
									grid.multiselected = new Array();
									var els = Ext.select('div[@class=x-grid-row-checker-checked]').elements;
									Ext.each(els, function(el, index){
										el.setAttribute('class','x-grid-row-checker');
									});
									this.selectAll(true);
									Ext.getCmp('set').setDisabled(false);
									Ext.getCmp('set1').setDisabled(false);
									Ext.getCmp('set2').setDisabled(false);
									header.el.addCls(Ext.baseCSSPrefix + 'grid-hd-checker-on');//添加这个
								}
							}
						}
					}),
					getMultiSelected: function(name){
						var grid = this;
						var items = grid.selModel.getSelection();
						Ext.each(items, function(item, index){
							if(this.data[grid.keyField] != null && this.data[grid.keyField] != ''
								&& this.data[grid.keyField] != '0' && this.data[grid.keyField] != 0){
								grid.multiselected.push(item);
							}
						});
						var records=Ext.Array.unique(grid.multiselected);
						var params = new Object();
						params.caller = caller;
						var data = new Array();
						Ext.each(records, function(record, index){
							if(grid.keyField && this.data[grid.keyField] != null && this.data[grid.keyField] != ''
								&& this.data[grid.keyField] != '0' && this.data[grid.keyField] != 0){
								bool = true;
								var o = new Object();
								o[grid.keyField] = record.data[grid.keyField];
								if(name){
									o[name] = record.data[name];
								}else{
									if(grid.necessaryFields){
										Ext.each(grid.necessaryFields, function(f, index){
											var v = record.data[f];
											if(Ext.isDate(v)){
												v = Ext.Date.toString(v);
											}
											o[f] = v;
										});
									}
								}
								data.push(o);
							}
						});
						params.data = Ext.encode(data);
						return params;
					}
				}
				]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});