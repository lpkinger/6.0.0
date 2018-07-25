/**
 * 批量更新回复日期按钮
 */	
Ext.define('erp.view.core.button.updateGridDetailReplyDate',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpUpdateGridDetailReplyDate',
		param: [],
		id: 'updateGridDetailReplyDate',
		text: $I18N.common.button.erpUpdateGridDetailReplyDate,
		iconCls: 'x-button-icon-save',
    	cls: 'x-btn-gray',
    	width: 140,
    	style: {
    		marginLeft: '10px'
        },
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});