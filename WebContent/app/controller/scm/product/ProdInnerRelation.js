Ext.QuickTips.init();
Ext.define('erp.controller.scm.product.ProdInnerRelation', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.FormUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil'],
    views:[
      		'scm.product.ProdInnerRelation','core.form.Panel','common.CommonPage','core.grid.Panel2','core.toolbar.Toolbar','core.trigger.MultiDbfindTrigger','core.trigger.HrOrgTreeDbfindTrigger',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit','core.button.FormBook',
      		'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail','core.button.ResSubmit',
      		'core.button.Scan','core.button.Banned','core.button.ResBanned','core.form.MultiField','core.button.Confirm','core.button.Sync',
      		'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField', 'core.grid.YnColumn','core.trigger.AddDbfindTrigger',
      		'core.form.FileField','core.form.SplitTextField','core.button.End','core.button.ResEnd','core.form.CheckBoxGroup','core.button.TurnMJProject',
      		'core.form.MonthDateField','core.form.SpecialContainField','core.form.SeparNumber','core.form.ConDateHourMinuteField','core.button.CommonTransfer',
      		'core.form.CheckBoxGroup','core.form.RadioGroup','core.button.PrintByCondition','core.button.Abate','core.button.ResAbate'
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
    				if(grid){
    					if(!(form.detailpercent && form.mainpercent && form.detailpercent>0 && form.mainpercent>0 && (form.detailpercent+form.mainpercent)==100)){
    						me.resize(form, grid);
    					}
    				}		
    			}    			
    		},
    		'erpGridPanel2': { 
    			itemclick: this.onGridItemClick,
    			reconfigure: function(grid){
    				var form = Ext.getCmp('form');
        			if(form){
        				if(!(form.detailpercent && form.mainpercent && form.detailpercent>0 && form.mainpercent>0 && (form.detailpercent+form.mainpercent)==100)){
        					me.resize(form, grid);
        				}
        				
        			}
        				
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
    		'erpBannedButton': {
				afterrender:function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'CANUSE' && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
				click: function(btn){
					var crid = Ext.getCmp(me.getForm(btn).keyField).value;
					me.FormUtil.onBanned(crid);
				}
			},
			'erpResBannedButton': {
				afterrender:function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'DISABLE'){
    					btn.hide();
    				}
    			},
				click: function(btn){
					var crid = Ext.getCmp(me.getForm(btn).keyField).value;
					me.FormUtil.onResBanned(crid);
				}
			},
			'erpEndButton': {
				afterrender:function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'CANUSE' && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
				click: function(btn){
					var crid = Ext.getCmp(me.getForm(btn).keyField).value;
					me.FormUtil.onEnd(crid);
				}
			},
			'erpResEndButton': {
				afterrender:function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'FINISH'){
    					btn.hide();
    				}
    			},
				click: function(btn){
					var crid = Ext.getCmp(me.getForm(btn).keyField).value;
					me.FormUtil.onResEnd(crid);
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
    		'erpPrintButton': {
    			click: function(btn){
    			var condition="";
    			var reportName="";
    			    var id = Ext.getCmp(me.getForm(btn).keyField).value;
    				me.FormUtil.onwindowsPrint(id, reportName, condition);
    			}
    		},
    		'htmleditor': {
    			afterrender: function(f){
    				f.setHeight(500);
    			}
    		},
    		'erpAbateButton':{//失效
    			afterrender: function(btn){
    				Ext.getCmp('erpAbateButton').setDisabled(true);
    			},
    			click: function(btn){
    				var grid = Ext.getCmp('grid'),win = me.remarkWin;
    				if (!win) {
    					win = me.remarkWin =me.creatRemarkWin(grid, 'abate');
    					win.show();
    				}else {
    					win.setTitle('失效备注');
    					win.show();
    				}
    			
	    		}
    		},
    		'erpResAbateButton':{//转有效
    			afterrender: function(btn){
    				Ext.getCmp('erpResAbateButton').setDisabled(true);
    			},
    			click: function(btn){
    				var grid = Ext.getCmp('grid'),win = me.remarkWin;
    				if (!win) {
    					win = me.remarkWin =me.creatRemarkWin(grid, 'resAbate');
    					win.show();
    				}else {
    					win.setTitle('转有效备注');
    					win.show();
    				}
	    		}
    		},
    	});
    }, 
    onGridItemClick: function(selModel, record){//grid行选择
    	this.GridUtil.onGridItemClick(selModel, record);
    	var ppdid = record.get('prd_id');
		if(ppdid != null && ppdid != 0) {
			var abate = Ext.getCmp('erpAbateButton');
			var res = Ext.getCmp('erpResAbateButton');
			if(record.data['prd_status']=='有效'){
				abate.setDisabled(false);
				res.setDisabled(true);
			}else{
				abate.setDisabled(true);
				res.setDisabled(false);
			}
		}
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
    },
    creatRemarkWin : function(grid, type) {
    	var title = type=='abate' ? '失效' : '转有效';
    	var win =Ext.create('Ext.window.Window', {
			closeAction : 'close',
			title : title + '备注',
			height: 150,
    		width: 230,
    		layout: 'border',
			items : [Ext.create('Ext.form.Panel', {
	    		region: 'center',
	    		anchor: '100% 100%',
	    		layout: 'fit',
	    		autoScroll: true,
	    		items:[{
	    	    	xtype: 'textareafield',
	    	    	labelWidth: 0,
	    	    	id: 'remark',
	    			fieldCls: 'x-form-field-cir',
	    			labelAlign : "right",
	    		}],
	    		bodyStyle: 'background:#f1f2f5;',
	    	}) ],
			buttonAlign : 'center',
			buttons : [{
				text : '确认',
				height : 26,
				iconCls: 'x-button-icon-check',
				handler : function(btn) {
					record = grid.selModel.lastSelected;
    				if(!record) {
    					return;
    				}
    				var prdid = record.get('prd_id'),remark = Ext.getCmp('remark'),message='';
    				if(prdid == null || prdid == 0) {
    					return;
    				}
    				if (remark && remark.value) {
    					message = remark.value;
    				}
	    			Ext.Ajax.request({
	        			url : basePath + "common/" + type + ".action?caller=ProdInnerRelation",
	        			params:{
	        				id: prdid,
	        				remark: message
	        			},
	        			method:'post',
	        			callback:function(options,success,response){
	        				var localJson = new Ext.decode(response.responseText);
	            			if(localJson.success){
	            				Ext.Msg.alert("提示","操作成功！");
	            				window.location.reload();
	            			} else {
	            				if(localJson.exceptionInfo){
	            	   				var str = localJson.exceptionInfo;
	            	   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
	            	   					str = str.replace('AFTERSUCCESS', '');
	            	   					showError(str);	            	   	
	            	   				} else {
	            	   					showError(str);return;
	            	   				}
	            	   			}
	            			}
	        			}
	        		});
				}
			},{
				text : '取消',
				iconCls: 'x-button-icon-delete',
				height : 26,
				handler : function(b) {
					var remark = Ext.getCmp('remark');
					if (remark) {
						remark.setValue();
					}
					b.ownerCt.ownerCt.close();
				}
			}],
		});
    	return win;
    }
});