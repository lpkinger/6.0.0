/**
 * 附件管理
 * @author liujw
 * @date 2017-12-06 19:01:49
 */	
Ext.define('erp.view.core.button.FormsDoc',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpFormsDocButton',
		id: 'erpFormsDocButton',
		text: $I18N.common.button.erpFormsDocButton,
		FormUtil : Ext.create('erp.util.FormUtil'),
		iconCls: 'x-button-icon-save',
    	cls: 'x-btn-gray',
    	width: 85,
    	hidden:false,
    	defaultOpen: true,
    	style: {
    		marginLeft: '10px'
        },
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		handler: function(btn){
			if(this.defaultOpen){
				var form = btn.ownerCt.ownerCt;
				if(form&&form.keyField&&caller){
					var id=Ext.getCmp(form.keyField);
					if(id&&id.value){
						var url = "jsps/common/formsdoc.jsp?whoami="+caller+"&formsid="+id.value;
						if(form.codeField&&Ext.getCmp(form.codeField)&&Ext.getCmp(form.codeField).value){
							url += "&formscode="+Ext.getCmp(form.codeField).value;
						}
		    			this.FormUtil.onAdd(caller+id.value, "附件管理", url);
					}else{
						showError(form.keyField+"不存在！");
					}
				}
			}
		}
	});