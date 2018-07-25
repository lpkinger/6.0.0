Ext.QuickTips.init();
Ext.define('erp.controller.common.JProcessTransfer', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'), 
    views:[
    		'core.form.Panel','common.JProcess.JprocessTransfer','core.grid.Panel2','core.toolbar.Toolbar','core.form.MultiField',
      		'core.button.Save','core.button.Add','core.button.Close','core.button.Delete','core.button.Update','core.button.Banned','core.button.ResBanned',  			
  		'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.form.StatusField'
    	],
    init:function(){
    	var me = this;   	
    	this.control({ 
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				var codevalue=Ext.getCmp(form.codeField).value;
    				if(codevalue == null ||codevalue == ''){
    					me.BaseUtil.getRandomNumber();//自动添加编号
    				}
    				me.FormUtil.beforeSave(this);
    			},
    			afterrender:function(btn){
    				var value=Ext.getCmp('jt_id').getValue();
    			    if(value){
    			    	btn.setDisabled(true);
    			    }
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				me.FormUtil.onUpdate(this);
    			},
    			afterrender:function(btn){
    				var value=Ext.getCmp('jt_id').getValue();
    			    if(!value){
    			    	btn.setDisabled(true);
    			    }
    			}
    		},
    		'messagebox':{
    			hide:function(btn){
    				var grid=Ext.getCmp('grid');
					var storeparam={
							caller:caller,
							condition:"Jt_id is not null"
					};
					me.GridUtil.loadNewStore(grid,storeparam);
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('jt_id').value);
    			},
    			afterrender:function(btn){
    				var value=Ext.getCmp('jt_id').getValue();
    			    if(!value){
    			    	btn.setDisabled(true);
    			    }
    			}
    		},
    		'hidden[id=jt_id]':{
    			change:function(field){
    			Ext.ComponentQuery.query('erpSaveButton')[0].hide();
    			Ext.ComponentQuery.query('erpDeleteButton')[0].setDisabled(false);
    			Ext.ComponentQuery.query('erpUpdateButton')[0].setDisabled(false);
    			
    			}
    		 },
    		 'hidden[id=jt_statuscode]':{
    			 change:function(field){
    				 if(field.value=='VALID'){
    					 Ext.ComponentQuery.query('erpResBannedButton')[0].setDisabled(true);
    					 Ext.ComponentQuery.query('erpBannedButton')[0].setDisabled(false);
    				 }else {
    					 Ext.ComponentQuery.query('erpResBannedButton')[0].setDisabled(false);
    					 Ext.ComponentQuery.query('erpBannedButton')[0].setDisabled(true);
    				 }
    			 }
    		 }, 	
    		'erpAddButton': {
    			click: function(btn){
    				me.getForm(btn).getForm().reset();
    				Ext.ComponentQuery.query('erpSaveButton')[0].show();
    				Ext.ComponentQuery.query('erpDeleteButton')[0].setDisabled(true);
        			Ext.ComponentQuery.query('erpUpdateButton')[0].setDisabled(true);
        			Ext.ComponentQuery.query('erpBannedButton')[0].setDisabled(true);
        			Ext.ComponentQuery.query('erpResBannedButton')[0].setDisabled(true);
    			}
    		},
    		'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
			'combo[id=jt_transfertype]':{
				change:function(combo,newvalue){
					var field=Ext.getCmp('jt_processdefid');
					if(field&&newvalue=='part'){
						field.setFieldStyle("background:#fffac0;color:#515151;");
					}else if(field&&newvalue!='part') {
						field.setFieldStyle("background:#FFFAFA;color:#515151;");
					}
				}
			},
			'erpBannedButton':{
				click:function(btn){
					me.FormUtil.onBanned(Ext.getCmp('jt_id').getValue());
				},
				afterrender:function(btn){
    				var value=Ext.getCmp('jt_statuscode').getValue();
    			    if(value!='VALID'){
    			    	btn.setDisabled(true);
    			    }
    			}
			},
			'erpResBannedButton':{
				click:function(btn){
					me.FormUtil.onResBanned(Ext.getCmp('jt_id').getValue());
				},
				afterrender:function(btn){
    				var value=Ext.getCmp('jt_statuscode').getValue();
    			    if(value=='VALID'){
    			    	btn.setDisabled(true);
    			    }
    			}
			},
			'erpGridPanel2':{
				itemclick:function( grid,record ){
					Ext.getCmp('form').getForm().setValues(record.data);
				}
			}
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}                   
});