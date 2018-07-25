/**
 * 明细行复制按钮
 */	
Ext.define('erp.view.core.button.Copy',{ 
		extend: 'Ext.Button', 
		alias: 'widget.copydetail',
		iconCls: 'x-button-icon-copy',
		cls: 'x-btn-tb',
    	tooltip: $I18N.common.button.erpCopyDetailButton,
    	disabled: true,
        //width: 65,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		handler: function(btn){
			/**
			 * 该按钮只支持单行复制,
			 * 有需求可添加多行复制功能 
			 */
			var grid = btn.ownerCt.ownerCt;
			var record = grid.selModel.lastSelected;
			if(record){
				var keys = Ext.Object.getKeys(record.data);
				var values = Ext.Object.getValues(record.data);
				var o = new Object();
				Ext.each(keys, function(key, index){
					if(key != grid.detno && key != grid.keyField){//排序字段和主键字段的值均不复制
						o[key] = values[index];
					}
				});
				grid.copyData = o;//需要粘贴时，直接取grid.copyData即可
			}
		}
	});