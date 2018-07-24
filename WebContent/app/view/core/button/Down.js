/**
 * 明细行下移按钮
 */	
Ext.define('erp.view.core.button.Down',{ 
		extend: 'Ext.Button', 
		alias: 'widget.downdetail',
		iconCls: 'x-button-icon-down',
		cls: 'x-btn-tb',
    	tooltip: $I18N.common.button.erpDownDetailButton,
    	disabled: true,
        //width: 65,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		handler: function(btn){
			var grid = btn.ownerCt.ownerCt;
			var record = grid.selModel.lastSelected;
			var fIdx = grid.store.indexOf(record);
			if(fIdx > -1) {
				var to = grid.store.getAt(fIdx + 1);
				if(to) {
					var keys = Ext.Object.getKeys(record.data);
					var v1 = Ext.Object.getValues(record.data);
					var v2 = Ext.Object.getValues(to.data);
					record.modified = {};
					to.modified = {};
					Ext.each(keys, function(key, index){
						if(key != grid.detno){//行编号不换
							record.set(key, v2[index]);
							to.set(key, v1[index]);
						}
					});
					//聚焦目标行
					grid.selModel.select(to);
				}
			}
		}
	});