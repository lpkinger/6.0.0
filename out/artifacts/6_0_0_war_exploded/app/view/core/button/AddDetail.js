/**
 * 明细行插入按钮
 */	
Ext.define('erp.view.core.button.AddDetail',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpAddDetailButton',
		iconCls: 'x-button-icon-detailadd',
		cls: 'x-btn-tb',
//    	id: 'adddetail',
    	tooltip: $I18N.common.button.erpAddButton,
    	disabled: true,
		initComponent : function(){ 
			this.callParent(arguments);
		},
		listeners: {
			afterrender: function(){
				this.grid = this.ownerCt.ownerCt;
			}
		},
		handler: function(btn){
			var me = this,
				grid = me.grid, store = grid.store,
				record = grid.selModel.lastSelected;
			if (record) {
				if(grid.detno) {
					var detno = Number(record.data[grid.detno]),d = detno;
					store.each(function(item){
						d = item.data[grid.detno];
						if(Number(d) > detno) {
							item.set(grid.detno, Number(d) + 1);
						}
					});
					var o = new Object();
					o[grid.detno] = detno + 1;
					grid.store.insert(store.indexOf(record) + 1, o);
				}
			}
		}
	});