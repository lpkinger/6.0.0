Ext.QuickTips.init();
Ext.define('erp.controller.pm.make.MakePlan!Add', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.FormUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil'],
    views:[
      		'pm.make.MakePlan!Add','core.form.Panel','core.grid.Panel2','core.toolbar.Toolbar','core.trigger.MultiDbfindTrigger','core.trigger.HrOrgTreeDbfindTrigger',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit','core.button.FormBook',
      		'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail','core.button.ResSubmit',
      		'core.button.Scan','core.button.Banned','core.button.ResBanned','core.form.MultiField','core.button.Confirm','core.button.Sync',
      		'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField', 'core.grid.YnColumn','core.trigger.AddDbfindTrigger',
      		'core.form.FileField','core.form.SplitTextField','core.button.End','core.button.ResEnd','core.form.CheckBoxGroup','core.button.TurnMJProject',
      		'core.form.MonthDateField','core.form.SpecialContainField','core.form.SeparNumber'
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
    		   'field[name=mp_kind]':{
	    		   		afterrender:function(f){
	    		   			if(Ext.getCmp('mp_period')){
	    		   				if(f.value=='年'){
	    		   				Ext.getCmp('mp_period').hide();
	    		   				Ext.getCmp('mp_period1').show();
	    		   				Ext.getCmp('mp_period2').hide();
		    		   			}else if(f.value=='日'){
	    		   				Ext.getCmp('mp_period').hide();
	    		   				Ext.getCmp('mp_period1').hide();
	    		   				Ext.getCmp('mp_period2').show();
		    		   			}else{
		    		   				Ext.getCmp('mp_period').show();
		    		   				Ext.getCmp('mp_period1').hide();
		    		   				Ext.getCmp('mp_period2').hide();
		    		   			}	
		    		   		}
	    		   		},
	    		   		change:function(f){
	    		   			/*var grid = Ext.getCmp('grid');
	    		   			if(Ext.getCmp('mp_period')){
	    		   			for(var i=0;i<grid.columns.length;i++){
	    		   				var k=grid.columns[i].dataIndex;;
	    		   				if(f.value=='年'){
	    		   				if(k=='week1' || k=='week2'||
	    		   				   k=='week3' || k=='week4'||
	    		   				   k=='week5'){
	    		   				   grid.columns[i].hide();
	    		   					}
	    		   				if(k=='mpd_datenum'){
		    		   				 grid.columns[i].show();
		    		   				}
		    		   			}else if(f.value=='月' || f.value=='日'){
	    		   				if(k=='week1' || k=='week2'||
	    		   				   k=='week3' || k=='week4'||
	    		   				   k=='week5' || k=='mpd_datenum'){
	    		   				   grid.columns[i].hide();
	    		   					}
		    		   			}else{
		    		   				if(k=='mpd_datenum'){
		    		   				 grid.columns[i].hide();
		    		   				}
		    		   			if(k=='week1' || k=='week2'||
	    		   				   k=='week3' || k=='week4'||
	    		   				   k=='week5'){
	    		   				   grid.columns[i].show();
	    		   					}
		    		   			}	
		    		   		}
	    		   			
	    		   			}*/
	    		   			if(Ext.getCmp('mp_period')){
	    		   				if(f.value=='年'){
	    		   				Ext.getCmp('mp_period').hide();
	    		   				Ext.getCmp('mp_period1').show();
	    		   				Ext.getCmp('mp_period2').hide();
		    		   			}else if(f.value=='日'){
	    		   				Ext.getCmp('mp_period').hide();
	    		   				Ext.getCmp('mp_period1').hide();
	    		   				Ext.getCmp('mp_period2').show();
		    		   			}else{
		    		   				Ext.getCmp('mp_period').show();
		    		   				Ext.getCmp('mp_period1').hide();
		    		   				Ext.getCmp('mp_period2').hide();
		    		   			}	
		    		   		}
	    		   		}
	    		   },
	/*    	'erpGridPanel2':{
	    		afterrender:function(f){
	    		Ext.defer(function(){
                  var grid = Ext.getCmp('grid');
                  var form = Ext.getCmp('mp_kind').value;
				if(form=='年'){
                  for(var i=0;i<grid.columns.length;i++){
		    		   	 	var k =grid.columns[i];
						if(k.dataIndex=='mpd_datenum'){
		    		   	 	k.show();
				    	 }
				    	 if(k.dataIndex=='week1'){
				    	    k.hide();
				    	 }
				    	 if(k.dataIndex=='week2'){
				    	    k.hide();
				    	 }
				    	 if(k.dataIndex=='week3'){
				    	    k.hide();
				    	 }
				    	 if(k.dataIndex=='week4'){
				    	    k.hide();
				    	 }
				    	 if(k.dataIndex=='week5'){
				    	    k.hide();
				    	 }
		    		}
	    		   }
	    		   if(form=='月' || form == '日'){
                  for(var i=0;i<grid.columns.length;i++){
		    		   	 	var k =grid.columns[i];
						if(k.dataIndex=='mpd_datenum'){
		    		   	 	k.hide();
				    	 }
				    	 if(k.dataIndex=='week1'){
				    	    k.hide();
				    	 }
				    	 if(k.dataIndex=='week2'){
				    	    k.hide();
				    	 }
				    	 if(k.dataIndex=='week3'){
				    	    k.hide();
				    	 }
				    	 if(k.dataIndex=='week4'){
				    	    k.hide();
				    	 }
				    	 if(k.dataIndex=='week5'){
				    	    k.hide();
				    	 }
		    		}
	    		   }
	    		   if(form=='周'){
                  for(var i=0;i<grid.columns.length;i++){
		    		   	 	var k =grid.columns[i];
						if(k.dataIndex=='mpd_datenum'){
		    		   	 	k.hide();
				    	 }
				    	 if(k.dataIndex=='week1'){
				    	    k.show();
				    	 }
				    	 if(k.dataIndex=='week2'){
				    	    k.show();
				    	 }
				    	 if(k.dataIndex=='week3'){
				    	    k.show();
				    	 }
				    	 if(k.dataIndex=='week4'){
				    	    k.show();
				    	 }
				    	 if(k.dataIndex=='week5'){
				    	    k.show();
				    	 }
		    		}
	    		   }
                   },1,this);
	    		}
	    	},*/
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