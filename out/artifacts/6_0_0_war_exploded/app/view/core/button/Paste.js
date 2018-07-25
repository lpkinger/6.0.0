/**
 * 明细行粘贴按钮
 */	
Ext.define('erp.view.core.button.Paste',{ 
		extend: 'Ext.Button', 
		alias: 'widget.pastedetail',
		iconCls: 'x-button-icon-paste',
		cls: 'x-btn-tb',
    	tooltip: $I18N.common.button.erpPasteDetailButton,
    	disabled: true,
        //width: 65,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		handler: function(btn){
			var grid = btn.ownerCt.ownerCt;
			var record = grid.selModel.lastSelected;
			if(record){
				var data = grid.copyData;
				if(data){//如果grid的剪切板有数据
					var keys = Ext.Object.getKeys(data);
					var values = Ext.Object.getValues(data);
					Ext.each(keys, function(key, index){
						record.set(key, values[index]);
					});
				}
			}
		}
	});