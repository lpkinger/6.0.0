Ext.QuickTips.init();
Ext.define('erp.controller.crm.chance.BusinessDataBase', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    RenderUtil:Ext.create('erp.util.RenderUtil'),
    views:[
    		'crm.chance.BusinessDataBase','core.form.Panel','core.form.FileField','core.form.MultiField',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.ResSubmit',
  			'core.button.Flow','core.button.DownLoad','core.button.Scan','common.datalist.Toolbar','core.form.ColorField',
  			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.form.HrOrgSelectField','core.form.ConDateHourMinuteField'
  	],
	init:function(){
		var me = this;
		this.control({
			'field[name=bd_name]' : {
				beforerender : function(f) {
					if (f.value != null && f.value != ''){
						f.editable=false;	
						f.readOnly=true;
					}
				}
			},
			'field[name=bd_prop]' : {
				afterrender : function(f) {
					if (f.value != null && f.value != '' && Ext.getCmp('bd_id') && Ext.getCmp('bd_id').value!=null && Ext.getCmp('bd_id').value!=''){
						f.editable=false;
						f.readOnly=true;
					}
					var a = Ext.getCmp('bd_prop').value;
					var b =Ext.getCmp("bd_browse");
					if(a=='管理员分配'){
						b.hide();
					}else if(a=='可领取可分配'){
						b.show()
					}
				},
					select: function(){
						var a = Ext.getCmp('bd_prop').value;
						var b =Ext.getCmp("bd_browse");
						if(a=='管理员分配'){
							b.hide();
						}else if(a=='可领取可分配'){
							b.show()
						}
						
					
				}
				
			},
			'field[name=bd_agency]':{
				beforerender:function(f){
					Ext.Ajax.request({    //查询代理商
						url : basePath + 'crm/chance/getAgency.action',
						params:{
							caller:"BusinessDataBase"
						},
						method : 'post',
						callback : function(options,success,response){
							var localJson = new Ext.decode(response.responseText);
							if(localJson.success){
								if (Ext.getCmp('bd_id') && (Ext.getCmp('bd_id').value==null || Ext.getCmp('bd_id').value==''))f.setValue(localJson.agentname);
								if(localJson.agentname!=null && localJson.agentname!="" && Ext.getCmp('bd_browse'))
								{Ext.apply(Ext.getCmp('bd_browse'),{
									isSpecial:true
								});}
							}else if(localJson.exceptionInfo){
								return;
							}
								
						}
					});
					}
			},

			'erpSaveButton' : {
				click: function(btn){
					//管理员分配的将bd_browseid字段值变成null
					if(Ext.getCmp("bd_prop").value=='管理员分配'){
						Ext.getCmp("bd_browseid").setValue(null);
					}
				me.beforeSave('save');
				}
			},
			'erpDeleteButton' : {
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('bd_id').value);
				}
			},
			'erpUpdateButton': {			
				click: function(btn){
					//管理员分配的将bd_browseid字段值变成null
					if(Ext.getCmp("bd_prop").value=='管理员分配'){
						Ext.getCmp("bd_browseid").setValue(null);
					}
					me.beforeSave('update');
				},
				afterrender:function(){
					if(Ext.getCmp("bd_prop").value=='管理员分配'){
						Ext.getCmp("bd_browse").hide();
						}
				}
			},
			'erpSubmitButton': {
				afterrender: function(btn){
					var statu = Ext.getCmp('bd_statuscode');
					if(statu && statu.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('bd_id').value);
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var statu = Ext.getCmp('bd_statuscode');
					if(statu && statu.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('bd_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var statu = Ext.getCmp('bd_statuscode');
					if(statu && statu.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('bd_id').value);
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var statu = Ext.getCmp('bd_statuscode');
					if(statu && statu.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('bd_id').value);
				}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addBusinessDataBase', '新增商机库', 'jsps/crm/chance/BusinessDataBase.jsp');
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			}
    	});
	},
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	beforeSave: function(type){
		var pro = Ext.getCmp('bd_prop'),admincode=Ext.getCmp('bd_admincode');
		/*if(pro && admincode && pro.value=="可领取可分配" && admincode.value!="") {admincode.setValue(null);Ext.getCmp('bd_admin').setValue(null);}
		else if (pro && admincode && pro.value=="管理员分配" && admincode.value==""){ showError("私有商机库管理员必填"); return;}*/
		if(type=='save')
			this.FormUtil.beforeSave(this);
		if(type=='update')
			this.FormUtil.onUpdate(this);
	}	
});