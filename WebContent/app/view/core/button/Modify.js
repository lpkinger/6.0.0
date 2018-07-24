/**
 * 修改按钮
 */	
Ext.define('erp.view.core.button.Modify',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpModifyCommonButton',
		iconCls: 'x-button-icon-modify',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpModifyCommonButton,
    	id:'modifybutton',
    	hidden:true,
    	style: {
    		marginLeft: '10px'
        },
        width: 90,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		listeners: {
			afterrender:function(btn){
				var form=Ext.getCmp('form');
				var statuscodeField=form.statuscodeField;
				var status = Ext.getCmp(statuscodeField);
					if(status && status.value!= 'ENTERING'){
						btn.show();
					}
			},
			show:function(btn){
				var form = btn.ownerCt.ownerCt;
				Ext.Array.forEach(form.items.items,function(item){
					if(item.modify){
						Ext.getCmp(item.id).setReadOnly(false);
						Ext.getCmp(item.id).setFieldStyle('background:#FFF;color:#515151');
					}
					if(item.id=='newStyle_Tab'){
						Ext.each(item.items.items,function(panel) {
							Ext.each(panel.items.items,function(item) {
								if(item.modify){
									Ext.getCmp(item.id).setReadOnly(false);
									Ext.getCmp(item.id).setFieldStyle('background:#FFF;color:#515151');
								}
							})
						});
					}
				});
			},
			click:function(btn){
				var form = Ext.getCmp('form'),params = new Object();;
				var s1 =form.FormUtil.checkFormDirty(form);
				if(form.codeField && (Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == '')){
					showError('编号不能为空.');
					return;
				}
				if(s1 == ''){
					showError('还未修改数据.');
					return;
				}
				if(form && form.getForm().isValid()){
					//form里面数据
					var r = form.getForm().getValues(false, true); 
					//去除ignore字段
					var keys = Ext.Object.getKeys(r), f;
					Ext.each(keys, function(k){
						f = form.down('#' + k);
						if(f && (f.logic == 'ignore'||!f.modify)) {
							delete r[k];
						}
						if(k == 'msg' && !f && r[k].indexOf('<img')>-1) {//照片字段剔除htmledit
							delete r[k];
						}	
					});
					if(form.keyField) {
						r[form.keyField] = form.down("#" + form.keyField).getValue();
					}
					Ext.each(Ext.Object.getKeys(r), function(k){//去掉页面非表单定义字段
						if(contains(k, 'ext-', true)){
							delete r[k];
						}
					});
					params.formStore = unescape(escape(Ext.JSON.encode(r)));
					params.caller=caller;
					form.setLoading(true);//loading...
					Ext.Ajax.request({
					    url : basePath + "oa/form/modify.action",
						params: params,
						method : 'post',
						callback : function(options,success,response){
							form.setLoading(false);
							var localJson = new Ext.decode(response.responseText);
							if(localJson.success){
								showMessage('提示', '保存成功!', 1000);
								var u = String(window.location.href);
								window.location.reload();
							}else {
								var str = localJson.exceptionInfo;
								showError(str);return;
							}
						}
					});
				}else{
					form.FormUtil.checkForm(form);
				}
			}
		}
	});