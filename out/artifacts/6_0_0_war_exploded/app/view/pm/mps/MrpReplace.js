Ext.define('erp.view.pm.mps.MrpReplace',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'anchor', 
				items: [{
					xtype: 'MrpReplaceGrid',
					anchor:'100% 100%',
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
				            me.selModel.select(items);
				        	if(bool){
				        		view.el.focus();
					        	var checkbox = item.childNodes[0].childNodes[0].childNodes[0];
					        	if(checkbox.getAttribute && checkbox.getAttribute('class') == 'x-grid-row-checker'){
					        		me.multiselected.push(record);
					        		items.push(record);
					        		me.selModel.select(items);
					        	} else {
					        		me.selModel.deselect(record);
					        		Ext.Array.remove(me.multiselected, record);
					        	}
				        	}
				        	me.summary();
				        },
				        onHeaderClick: function(headerCt, header, e) {
				            if (header.isCheckerHd) {
				                e.stopEvent();
				                var isChecked = header.el.hasCls(Ext.baseCSSPrefix + 'grid-hd-checker-on');
				                if (isChecked) {
				                    this.deselectAll(true);
				                    var grid = Ext.getCmp('batchDealGridPanel');
				                    this.deselect(grid.multiselected);
				                    grid.multiselected = new Array();
				                    var els = Ext.select('div[@class=x-grid-row-checker-checked]').elements;
					                Ext.each(els, function(el, index){
					                	el.setAttribute('class','x-grid-row-checker');
					                });
				                    header.el.removeCls(Ext.baseCSSPrefix + 'grid-hd-checker-on');//添加这个
				                } else {
				                	var grid = Ext.getCmp('batchDealGridPanel');
				                	this.deselect(grid.multiselected);
					                grid.multiselected = new Array();
					                var els = Ext.select('div[@class=x-grid-row-checker-checked]').elements;
					                Ext.each(els, function(el, index){
					                	el.setAttribute('class','x-grid-row-checker');
					                });
				                    this.selectAll(true);
				                    header.el.addCls(Ext.baseCSSPrefix + 'grid-hd-checker-on');//添加这个
				                }
				            }				      
				        }
				})	
				}] 
			}]
		}); 
		me.callParent(arguments); 
	} 
});