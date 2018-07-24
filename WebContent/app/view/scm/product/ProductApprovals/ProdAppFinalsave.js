/**
 * 部分保存认定单
 */	
Ext.define('erp.view.scm.product.ProductApprovals.ProdAppFinalsave',{ 
		extend: 'Ext.Button', 
		alias: 'widget.ProdAppFinalsaveButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text:'认定结果保存',
    	style: {
    		marginLeft: '10px'
        },
        width: 120,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		handler:function(){
			var result=Ext.getCmp('pa_finalresult').value;
			if(result==''){
				showError('请填写认定结果！');
				return;
			}
		}
	});