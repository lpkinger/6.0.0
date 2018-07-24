Ext.QuickTips.init();
Ext.define('erp.controller.oa.device.DeviceKind', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.FormUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil'],
    views:[
      		'oa.device.DeviceKind','core.form.Panel','core.grid.Panel2','core.toolbar.Toolbar','core.trigger.MultiDbfindTrigger','core.trigger.HrOrgTreeDbfindTrigger',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit','core.button.FormBook',
      		'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail','core.button.ResSubmit',
      		'core.button.Scan','core.button.Banned','core.button.ResBanned','core.form.MultiField','core.button.Confirm','core.button.Sync',
      		'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField', 'core.grid.YnColumn','core.trigger.AddDbfindTrigger',
      		'core.form.FileField','core.form.SplitTextField','core.button.End','core.button.ResEnd','core.form.CheckBoxGroup','core.button.TurnMJProject',
      		'core.form.MonthDateField','core.form.SpecialContainField','core.form.SeparNumber','core.button.AutoInvoice'
      	],
    init:function(){
    	var me = this;
    	me.FormUtil = Ext.create('erp.util.FormUtil');
    	me.GridUtil = Ext.create('erp.util.GridUtil');
    	me.BaseUtil = Ext.create('erp.util.BaseUtil');
		this.control({
    		'erpFormPanel': {
    			alladded: function(form){
    				var grid = Ext.getCmp('grid');
    				if(grid)
    					me.resize(form, grid);
    			}    			
    		},
    		'erpGridPanel2': { 
    			itemclick: this.onGridItemClick,
    			reconfigure: function(grid){
    				var form = Ext.getCmp('form');
        			if(form)
        				me.resize(form, grid);
    			}
    		},
    		'erpSaveButton': {
    			afterrender: function(btn){
    				var form = me.getForm(btn);
    				var codeField = Ext.getCmp(form.codeField);  
    				if(Ext.getCmp(form.codeField) && (Ext.getCmp(form.codeField).value != null && Ext.getCmp(form.codeField).value != '')){
    						btn.hide();
    					}
    			},
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(!Ext.isEmpty(form.codeField) && Ext.getCmp(form.codeField) && ( 
    						Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == '')){
    					me.BaseUtil.getRandomNumber(caller);//自动添加编号
    				}
    				this.FormUtil.beforeSave(this);
    			}
    		},
    		'erpDeleteButton' : {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp(me.getForm(btn).keyField).value);
    			}
    		},
    		'erpUpdateButton': {
    				click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpAddButton': {
    			click: function(btn){
    				var title = btn.ownerCt.ownerCt.title || ' ';
    				var url = window.location.href;
    				url = url.replace(basePath, '');
    				url = url.substring(0, url.lastIndexOf('formCondition')-1);
    				me.FormUtil.onAdd('add' + caller, title, url);
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
     				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp(me.getForm(btn).keyField).value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp(me.getForm(btn).keyField).value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp(me.getForm(btn).keyField).value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp(me.getForm(btn).keyField).value);
    			}
    		},
    		'dbfindtrigger[name=mp_linecode]': {
				beforetrigger: function(t){
    				var wccode = Ext.getCmp('mp_wccode');
					if(wccode && wccode.value !='' && wccode.value != null){
						t.dbBaseCondition = " li_wccode='"+ wccode.value+"'";
				  }
    			}
    		},
    		'erpAutoInvoiceButton': {
    			afterrender: function(btn){
    				btn.setWidth(90);
    				btn.setText('自动生成');
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				var mp_workshift = Ext.getCmp('mp_workshift');
    				var mp_planqty = Ext.getCmp('mp_planqty');
    				if(mp_workshift && (mp_workshift.value == '' || mp_workshift.value == null)){
    					showError("未填写班制!");
    					return;
    				}else if(mp_planqty && (mp_planqty.value== '' || mp_planqty.value == null)){
    					showError("未填写排产数量!");
    					return;
    				}
    				var Bz = mp_workshift.value;  //  班制
    				var planqty = mp_planqty.value; //计划产能
    				var linecode = '';
    				if(Ext.getCmp('mp_linecode')){
    					linecode = Ext.getCmp('mp_linecode').value;
    				}
    				var grid = Ext.getCmp('grid');
    				Ext.Array.each(grid.getStore().data.items,function(item){
    					if(item.data['mpd_planqty'] == null || item.data['mpd_planqty'] == 0 || item.data['mpd_planqty'] == '' ){
    						item.set('mpd_planqty',planqty);
    					} 
    					if(item.data['mpd_datenum'] == null || item.data['mpd_datenum'] == '' ){
    						item.set('mpd_datenum','H'+item.data['mpd_detno']);
    					} 
    					    item.set('mpd_linecode',linecode);
    				});
    				
    				/*this.onUpdate();*/
    			}
    		}
			})
			},
    		
    onGridItemClick: function(selModel, record){//grid行选择
    	this.GridUtil.onGridItemClick(selModel, record);
    },
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
    resize: function(form, grid){
    	if(!this.resized && form && grid && form.items.items.length > 0){
    		var height = window.innerHeight, 
    			fh = form.getEl().down('.x-panel-body>.x-column-inner').getHeight();
		
			form.setHeight(70 + fh);
			grid.setHeight(height - fh - 70);
			this.resized = true;
		}
    }
});