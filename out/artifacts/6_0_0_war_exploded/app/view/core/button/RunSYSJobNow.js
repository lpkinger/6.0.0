/**
 * 立即执行ORACLE JOB
 */	
Ext.define('erp.view.core.button.RunSYSJobNow',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpRunSYSJobNowButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	id: 'runsysjobnowbtn',
    	text: $I18N.common.button.erpRunSYSJobNowButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 90,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});