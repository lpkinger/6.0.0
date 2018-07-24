Ext.QuickTips.init();
Ext.define('erp.controller.fs.credit.ResultsReport', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'fs.credit.ResultsReport','core.form.Panel','core.grid.Panel2','core.toolbar.Toolbar','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close','core.button.Print',
			'core.button.Upload','core.button.Update','core.button.Delete','core.button.ResAudit','core.button.ResSubmit','core.form.HrefField',
			'core.form.YnField','core.button.Close','core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.MultiField'
    	],
        init:function(){
        	var me = this;
        	this.control({ 
        		'gridcolumn[dataIndex=rr_result]': {
        			beforerender: function(column){
        				var status = Ext.getCmp('re_statuscode');
        				var bool = false;
        				if(status&&status.value!='ENTERING'){
        					bool = true;
        				}
        				column.processEvent = function(type, view, cell, recordIndex, cellIndex, e) {
        					var record = view.panel.store.getAt(recordIndex);
				        	var line = record.get('rr_kind') == 'LINE';
				        	if(bool||line){
				        		 return false;
				        	}
					        if (type == 'mousedown' || (type == 'keydown' && (e.getKey() == e.ENTER || e.getKey() == e.SPACE))) {
					        	var dataIndex = this.dataIndex;
					        	var checked = record.get(dataIndex)==1?0:1;
					            record.set(dataIndex, checked);
					            this.fireEvent('checkchange', this, recordIndex, checked);
					            return false;
					        } 
					    };
        				column.renderer = function(value, m, record){
					        var cssPrefix = Ext.baseCSSPrefix,cls;
					        var line = record.get('rr_kind') == 'LINE';
					        if(bool||line){
					        	if(value){
					        		cls= ['checked'];
					        	}else{
					        		cls= ['error'];
					        	}
					        }else{
					        	cls= [cssPrefix + 'grid-checkheader'];
						        if (value) {
						            cls.push(cssPrefix + 'grid-checkheader-checked');
						        }
					        }
					         return '<div class="' + cls.join(' ') + '">&#160;</div>';
					    };
        			}
        		},
        		'erpCloseButton': {
        			click: function(btn){
        				this.FormUtil.beforeClose(this);
        			}
        		},
        		'erpUpdateButton': {
        			click: function(btn){
        				this.FormUtil.onUpdate(this);	
        			}
        		},
        		'erpDeleteButton': {
        			click: function(btn){
        				me.FormUtil.onDelete((Ext.getCmp('re_id').value));
        			}
        		},
          		'erpSubmitButton': {
        			afterrender: function(btn){
        				var status = Ext.getCmp('re_statuscode');
        				if(status && status.value != 'ENTERING'){
        					btn.hide();
        				}
        			},
        			click: function(btn){
        				me.FormUtil.onSubmit(Ext.getCmp('re_id').value);
        			}
        		},
        		'erpResSubmitButton': {
        			afterrender: function(btn){
        				var status = Ext.getCmp('re_statuscode');
        				if(status && status.value != 'COMMITED'){
        					btn.hide();
        				}
        			},
        			click: function(btn){
        				me.FormUtil.onResSubmit(Ext.getCmp('re_id').value);
        			}
        		},
        		'erpAuditButton': {
        			afterrender: function(btn){
        				var status = Ext.getCmp('re_statuscode');
        				if(status && status.value != 'COMMITED'){
        					btn.hide();
        				}
        			},
        			click: function(btn){
        				me.FormUtil.onAudit(Ext.getCmp('re_id').value);
        			}
        		},
        		'erpResAuditButton' : {
					afterrender : function(btn) {
						var status = Ext.getCmp('re_statuscode');
						if (status && status.value != 'AUDITED') {
							btn.hide();
						}
					},
					click : function(btn) {
						me.FormUtil.onResAudit(Ext.getCmp('re_id').value);
					}
				}		
        	});
        }
});