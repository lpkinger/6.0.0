/**
 * 新增上架按钮
 */	
Ext.define('erp.view.core.button.GoodsUp',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpGoodsUpButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	id: 'goodsupbtn',
    	text: $I18N.common.button.erpGoodsUpButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 60,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});