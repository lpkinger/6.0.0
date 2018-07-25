Ext.define('erp.view.oa.attention.AttentionGrade',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){
		var me = this; 
		Ext.apply(me, { 
		items: [{
	    	  region: 'south',         
	    	  xtype:'erpAttentionGridPanel',  
	    	  anchor: '100% 100%',
	    	 dockedItems: [{
                 xtype: 'toolbar',
                 dock: 'top',
                 style:'font-size:16px;height:40px',
			     bodyStyle: 'font-size:16px;height:40px',
                 items: [{
                 xtype: 'button',
                 iconCls: 'tree-add',
                 id:'add',
		         text: $I18N.common.button.erpAddButton,
		         style:'margin-left:10px'
	              },{
	              xtype: 'button',
	              id:'delete',
                  iconCls: 'tree-delete',
                  disabled:true,
		         text: $I18N.common.button.erpDeleteButton,
		         style:'margin-left:10px'
	              }]
	              }],
	               selModel: Ext.create('Ext.selection.CheckboxModel',{
    	ignoreRightMouseSelection : false,
		listeners:{
            selectionchange:function(selectionModel, selected, options){
          
            }
        },
        onRowMouseDown: function(view, record, item, index, e) {//改写的onRowMouseDown方法
        	var me = Ext.getCmp('AttentionGridPanel');
        	var bool = true;
        	var items = me.selModel.getSelection();
            Ext.each(items, function(item, index){
            	if(item.data == record.data){
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
	        	if(checkbox.getAttribute('class') == 'x-grid-row-checker'){
	        		me.multiselected.push(record);
	        		items.push(record);
	        		me.selModel.select(items);
	        	} else {
	        		me.selModel.deselect(record);
	        		Ext.Array.remove(me.multiselected, record);
	        	}
        	}
        	   if(items.length>0){
	        	   Ext.getCmp('delete').setDisabled(false);
	        	}else {Ext.getCmp('delete').setDisabled(true);	        	     
	        	}
        },
        onHeaderClick: function(headerCt, header, e) {
        	if (header.isCheckerHd) {
                e.stopEvent();
                var isChecked = header.el.hasCls(Ext.baseCSSPrefix + 'grid-hd-checker-on');
                if (isChecked) {
                    this.deselectAll(true);
                    var grid = Ext.getCmp('AttentionGridPanel');
                    this.deselect(grid.multiselected);
                    grid.multiselected = new Array();
                    var els = Ext.select('div[@class=x-grid-row-checker-checked]').elements;
	                Ext.each(els, function(el, index){
	                	el.setAttribute('class','x-grid-row-checker');
	                });
                   header.el.removeCls(Ext.baseCSSPrefix + 'grid-hd-checker-on');//添加这个
                } else {
                header.el.addCls(Ext.baseCSSPrefix + 'grid-hd-checker-on');//添加这个
                	var grid = Ext.getCmp('AttentionGridPanel');
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
	}), 
	             }] 
		});
		me.callParent(arguments); 
	}
});