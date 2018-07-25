/**
 * 保存按钮
 * 适用于单据新增页面的保存，
 * 使用时，只需传递一个提交后台的saveUrl即可
 * @author guq
 * @date 2018-03-026 10:45:49
 */	
Ext.define('erp.view.core.button.RefreshSendDate',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpRefreshSendDateButton',
		param: [],
		id: 'erpRefreshSendDateButton',
		text: '更新实际发车时间',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	formBind: true,//form.isValid() == false时,按钮disabled
    	width: 140,
    	style: {
    		marginLeft: '10px'
        },
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});