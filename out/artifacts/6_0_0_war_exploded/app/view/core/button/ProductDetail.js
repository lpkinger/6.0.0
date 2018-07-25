/**
 *制造单、委外单：出入库明细
 */	
Ext.define('erp.view.core.button.ProductDetail',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpProductDetailButton',
		iconCls: 'x-button-icon-yuan',
    	cls: 'x-btn-gray',
    	id: 'productdetail',
    	text: $I18N.common.button.erpProductDetailButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 130,
		initComponent : function(){ 
			this.callParent(arguments); 
			this.addEvents({
				base: true,
				formal: true
			});
		}
	});