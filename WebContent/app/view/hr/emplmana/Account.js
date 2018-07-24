Ext.define('erp.view.hr.emplmana.Account',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 30%',
					updateUrl: 'hr/emplmana/updateemAccount.action',		
					getIdUrl: 'common/getId.action?seq=Account_SEQ',
					auditUrl: 'hr/employee/auditEmployee.action',
					submitUrl: 'hr/employee/submitEmployee.action',
					resSubmitUrl: 'hr/employee/resSubmitEmployee.action',
					resAuditUrl: 'hr/employee/resAuditEmployee.action',
					keyField: 'em_id',
					statusField: 'em_statuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 70%', 
					keyField: 'es_id',
					mainField: 'es_emid',
					multiselected:[]
				/*	selModel: Ext.create('Ext.selection.CheckboxModel',{
				    	ignoreRightMouseSelection : false,
						listeners:{
				            selectionchange:function(selModel, selected, options){
				            	selModel.view.ownerCt.selectall = false;
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
				            	if(this.index == record.index){
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
				                    header.el.removeCls(Ext.baseCSSPrefix + 'grid-hd-checker-on');//添加这个
				                } else {
				                	var grid = Ext.getCmp('grid');
				                	this.deselect(grid.multiselected);
					                grid.multiselected = new Array();
					                var els = Ext.select('div[@class=x-grid-row-checker-checked]').elements;
					                Ext.each(els, function(el, index){
					                	el.setAttribute('class','x-grid-row-checker');
					                });
				                    this.selectAll(true);
				                    this.view.ownerCt.selectall = true;
				                    header.el.addCls(Ext.baseCSSPrefix + 'grid-hd-checker-on');//添加这个
				                }
				            }
				        }	
				    })*/
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});