/**
 * 新增按钮
 */	
Ext.define('erp.view.core.button.ModelScore',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpModelScoreButton',
		iconCls: 'x-button-icon-code',
    	cls: 'x-btn-gray',
    	id: 'modelscorebtn',
    	text: '模型得分',
    	style: {
    		marginLeft: '10px'
        },
        width: 85,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});