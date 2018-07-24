/**
 * 保存按钮
 * 适用于单据新增页面的保存，
 * 使用时，只需传递一个提交后台的saveUrl即可
 * @author yingp
 * @date 2012-08-03 10:45:49
 */	
Ext.define('erp.view.core.button.Save',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpSaveButton',
		param: [],
		id: 'save',
		text: $I18N.common.button.erpSaveButton,
		iconCls: 'x-button-icon-save',
    	cls: 'x-btn-gray',
    	formBind: true,//form.isValid() == false时,按钮disabled
    	width: 60,
    	style: {
    		marginLeft: '10px'
        },
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});