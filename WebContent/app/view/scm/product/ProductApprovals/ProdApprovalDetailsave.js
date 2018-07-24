/**
 * 部分保存认定单
 */	
Ext.define('erp.view.scm.product.ProductApprovals.ProdApprovalDetailsave',{ 
		extend: 'Ext.Button', 
		alias: 'widget.ProdApprovalDetailsaveButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: '研发认定保存',
    	style: {
    		marginLeft: '10px'
        },
        width: 120,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});