Ext.QuickTips.init();
Ext.define('erp.controller.ma.logic.Enterprise', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
    		'ma.logic.Enterprise','core.form.Panel','core.form.FileField','core.form.MultiField','core.form.CheckBoxGroup','core.trigger.MultiDbfindTrigger',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit','core.form.PhotoField',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.ResSubmit','core.grid.Panel2',
  			'core.button.TurnCustomer','core.button.Flow','core.button.DownLoad','core.button.Scan','common.datalist.Toolbar','core.button.Confirm',
  			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.trigger.AutoCodeTrigger','core.trigger.AddDbfindTrigger'
  	],
	init:function(){
		var me = this;
		this.control({
			'erpGridPanel2': { 
				itemclick: this.onGridItemClick
			},
			'erpSaveButton': {
				click: function(btn){
					var form = me.getForm(btn);
					if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
						me.BaseUtil.getRandomNumber();//自动添加编号
					}
					//保存之前的一些前台的逻辑判定
					this.FormUtil.beforeSave(this);
				}
			},
			'erpDeleteButton' : {
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('en_id').value);
				}
			},
			'erpUpdateButton': {
				click: function(btn){
					this.FormUtil.onUpdate(this,true);
				}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addKBIbill', '新增KBI考评人申请', 'jsps/hr/kbi/kBIbill.jsp');
				}
			},'erpSubmitButton': {
				afterrender: function(btn){
					btn.setText("一键注册");
					btn.setWidth(100);
					var status = Ext.getCmp('en_uu');
					if(status && status.value >0){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('en_id').value);
				}
			},'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('en_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('en_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('en_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('en_id').value);
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('en_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('en_id').value);
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
			'#11':{
				afterrender:function(f){
					f.setTitle(f.title+'(*请上传大小小于20k的图片)');
					f.upload=function(form, field){
						var filename = '';
						if(contains(field.value, "\\", true)){
							filename = field.value.substring(field.value.lastIndexOf('\\') + 1);
						} else {
							filename = field.value.substring(field.value.lastIndexOf('/') + 1);
						}
						form.getForm().submit({
							url: basePath + 'ma/logic/uploadLogo.action?id='+Ext.getCmp('en_id').value,
							waitMsg: "正在上传:" + filename,
							success: function(fp, o){
									Ext.Msg.alert('提示','上传成功!',function(){
										window.location.reload();
									});  
							},
							failure: function(fp, o) {
				                Ext.Msg.alert('提示','上传失败，请检查文件大小及格式!');  
				            } 
						});
					};
					f.insert(2,
						{xtype: 'image',
						 src:'',
						 id:'logo',
						 margins:'10',
						 hide:true});
					Ext.Ajax.request({
						url: basePath + 'ma/logic/hasLogo.action',
						success:function(re, o,rep){
							if(re.responseText=='true') {
								Ext.getCmp('logo').setSrc(basePath+'ma/logic/getLogo.action');
								Ext.getCmp('logo').show();
							}
						}						
					});
				}
			}
    	});
	},
	onGridItemClick: function(selModel, record){//grid行选择
    	this.GridUtil.onGridItemClick(selModel, record);
    },
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});