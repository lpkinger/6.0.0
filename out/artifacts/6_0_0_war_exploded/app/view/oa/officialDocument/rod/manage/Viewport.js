Ext.define('erp.view.oa.officialDocument.rod.manage.Viewport',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
		    	  region: 'north',         
		    	  xtype:'erpRODManageFormPanel',  
		    	  anchor: '100% 30%'
		    },{
		    	  region: 'south',         
		    	  xtype:'erpDatalistGridPanel',  
		    	  anchor: '100% 70%',
		    	  multiselected: [],
		    	  selModel: Ext.create('Ext.selection.CheckboxModel',{
//		    		  renderer: function(value, metaData, record, rowIndex, colIndex, store, view) {
//		    	            if(record.data.rod_statuscode != 'OVERED'){
//		    	            	metaData.tdCls = Ext.baseCSSPrefix + 'grid-cell-special';
//		    	            	return '<div class="' + Ext.baseCSSPrefix + 'grid-row-checker">&#160;</div>';            	
//		    	            }
//		    	      },
//		    		  onRowMouseDown: function(view, record, item, index, e) {//改写的onRowMouseDown方法
//		  	        	var me = Ext.getCmp('grid');
//		  	        	var bool = true;
//		  	        	var items = me.selModel.getSelection();
//		  	            Ext.each(items, function(item, index){
//		  	            	if(this.index == record.index){
//		  	            		bool = false;
//		  	            		me.selModel.deselect(record);
//		  	            		Ext.Array.remove(items, item);
//		  	            		Ext.Array.remove(me.multiselected, record);
//		  	            	}
//		  	            });
//		  	            Ext.each(me.multiselected, function(item, index){
//		  	            	items.push(item);
//		  	            });
//		  	            me.selModel.select(items);
//		  	        	if(bool){
//		  	        		view.el.focus();
//		  		        	var checkbox = item.childNodes[0].childNodes[0].childNodes[0];
//		  		        	if(checkbox.getAttribute('class') == 'x-grid-row-checker'){
//		  		        		//checkbox.setAttribute('class','x-grid-row-checker-checked');//只是修改了其样式，并没有将record加到selModel里面
//		  		        		if(record.data.rod_statuscode == 'OVERED'){
//		  		        			me.selModel.deselect(record);
//			  		        		Ext.Array.remove(me.multiselected, record);
//		  		        		} else {
//		  		        			me.multiselected.push(record);
//		  		        			items.push(record);
//		  		        			me.selModel.select(items);		  		        			
//		  		        		}
//		  		        	} else {
//		  		        		me.selModel.deselect(record);
//		  		        		Ext.Array.remove(me.multiselected, record);
//		  		        		//checkbox.setAttribute('class','x-grid-row-checker');
//		  		        	}
//		  	        	}
//		  	        },
//		  	        onHeaderClick: function(headerCt, header, e) {
//		  	            if (header.isCheckerHd) {
//		  	                e.stopEvent();
//		  	                var isChecked = header.el.hasCls(Ext.baseCSSPrefix + 'grid-hd-checker-on');
//		  	                if (isChecked) {
//		  	                    this.deselectAll(true);
//		  	                    var grid = Ext.getCmp('grid');
//		  	                    this.deselect(grid.multiselected);
//		  	                    grid.multiselected = new Array();
//		  	                    var els = Ext.select('div[@class=x-grid-row-checker-checked]').elements;
//		  		                Ext.each(els, function(el, index){
//		  		                	el.setAttribute('class','x-grid-row-checker');
//		  		                });
//		  	                    header.el.removeCls(Ext.baseCSSPrefix + 'grid-hd-checker-on');//添加这个
//		  	                } else {
//		  	                	var grid = Ext.getCmp('grid');
//		  	                	this.deselect(grid.multiselected);
//		  		                grid.multiselected = new Array();
//		  		                var els = Ext.select('div[@class=x-grid-row-checker-checked]').elements;
//		  		                Ext.each(els, function(el, index){
//		  		                	el.setAttribute('class','x-grid-row-checker');
//		  		                });
//		  	                    this.selectAll(true);
//		  	                    var items = grid.selModel.getSelection();
//		  	                    Ext.each(items, function(item, index){
//		  	                    	if(item.data.rod_statuscode == 'OVERED'){
//			  		        			grid.selModel.deselect(item);
//			  		        		} 
//		  		                });
////		  	                    console.log(grid.selModel.getSelection());
//		  	                    header.el.addCls(Ext.baseCSSPrefix + 'grid-hd-checker-on');//添加这个
//		  	                }
//		  	            }
//		  	        }
		    	  }),
		    	  tbar:[ {
		    	    	iconCls: 'group-delete',
		    	    	id: 'delete',
		    			text: $I18N.common.button.erpDeleteButton
		    	  }]
		    }]
		});
		me.callParent(arguments); 
	}
});