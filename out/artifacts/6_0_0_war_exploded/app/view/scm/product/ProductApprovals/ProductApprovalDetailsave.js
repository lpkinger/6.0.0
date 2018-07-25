/**
 * 部分保存认定单
 */	
Ext.define('erp.view.scm.product.ProductApprovals.ProductApprovalDetailsave',{ 
		extend: 'Ext.Button', 
		alias: 'widget.ProductApprovalDetailsaveButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text:'IQC认定保存',
    	style: {
    		marginLeft: '10px'
        },
        width: 120,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});