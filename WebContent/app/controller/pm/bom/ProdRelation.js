Ext.QuickTips.init();
Ext.define('erp.controller.pm.bom.ProdRelation', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','pm.bom.ProdRelation','core.grid.Panel2','core.toolbar.Toolbar','core.form.MultiField',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.ResAudit',
      		'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail',
      		'core.button.ResSubmit','core.button.Banned','core.button.ResBanned',
			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField'
      	],
    init:function(){
    	var me = this;
    	this.control({
    		'erpGridPanel2': { 
    			itemclick: this.onGridItemClick
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				var me = this;
    				var form = me.getForm(btn);
    				var grid = Ext.getCmp('grid');
    				if(grid.GridUtil.isEmpty(grid)) {
    					showError('明细行未填写，不允许保存!');
    					return;
    				}	
    			  var  c = this.getMixedGroups(grid.store.data.items, 'prr_repcode');
    			   if(c.length != grid.getStore().getCount()){
    			   	showError('替代料编号重复，不允许保存!');
    			    return ;
    			    }
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();//自动添加编号
    				}
    				this.FormUtil.beforeSave(this);
    			}
    		},
    		'erpDeleteButton' : {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('prr_thisid').value);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    			var grid = Ext.getCmp('grid');
    			var  c = this.getMixedGroups(grid.store.data.items, 'prr_repcode');
    			   if(c.length != grid.getStore().getCount()){
    			   	showError('替代料编号重复，不允许更新!');
    			    return ;
    			    }
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addProdRelation', '新增物料标准替代库', 'jsps/pm/bom/prodRelation.jsp');
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('prr_usestatuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				var me = this;
    				var id= Ext.getCmp('prr_thisid').value;
    				me.FormUtil.onSubmit(Ext.getCmp('prr_thisid').value,true);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('prr_usestatuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				var me = this;
    				me.FormUtil.onResSubmit(Ext.getCmp('prr_thisid').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('prr_usestatuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('prr_thisid').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('prr_usestatuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				var me = this;
    				me.FormUtil.onResAudit(Ext.getCmp('prr_thisid').value);
    			}
    		},
    		'erpBannedButton': {
    			click: function(m){
    				var me = this;
    				me.turn('ProdRelation!Banned', ' prr_thisid=' + Ext.getCmp('prr_thisid').value +' and prr_usestatuscode=\'AUDITED\'', 'pm/bom/bannedProdRelation.action');
            	},
            	afterrender:function(btn){
            		var status = Ext.getCmp('prr_usestatuscode');
    				if(status && status.value != 'AUDITED' && status.value != 'DISABLE'){
    					btn.hide();
    				}
            	}
    		},
    		'dbfindtrigger[name=ra_topid]':{
    			afterrender: function(){
    				var id = Ext.getCmp('prr_thisid').value;
    				if(id != null & id != ''){
    					this.getProdRelationStore('prr_thisid=' + id);
    				}
    			},
    			aftertrigger: function(){
    				var id = Ext.getCmp('prr_thisid').value;
    				if(id != null & id != ''){
    					this.getProdRelationStore('prr_thisid=' + id);
    				}
    			}
    		}
    	});
    }, 
    onGridItemClick: function(selModel, record){//grid行选择
    	this.GridUtil.onGridItemClick(selModel, record);
    },
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	turn: function(nCaller, condition, url){
    	var win = new Ext.window.Window({
	    	id : 'win',
			    height: "100%",
			    width: "80%",
			    maximizable : true,
				buttonAlign : 'center',
				layout : 'anchor',
			    items: [{
			    	  tag : 'iframe',
			    	  frame : true,
			    	  anchor : '100% 100%',
			    	  layout : 'fit',
			    	  html : '<iframe id="iframe_' + caller + '" src="' + basePath + 'jsps/common/editorColumn.jsp?caller=' + nCaller 
			    	  	+ "&condition=" + condition +'" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
			    }],
			    buttons : [{
			    	name: 'confirm',
			    	text : $I18N.common.button.erpConfirmButton,
			    	iconCls: 'x-button-icon-confirm',
			    	cls: 'x-btn-gray',
			    	listeners: {
				    		buffer: 500,
				    		click: function(btn) {
				    			var grid = Ext.getCmp('win').items.items[0].body.dom.getElementsByTagName('iframe')[0].contentWindow.Ext.getCmp("editorColumnGridPanel");
				    			btn.setDisabled(true);
				    			grid.updateAction(url);
				    			window.location.reload();
				    		}
			    	}
			    }, {
			    	text : $I18N.common.button.erpCloseButton,
			    	iconCls: 'x-button-icon-close',
			    	cls: 'x-btn-gray',
			    	handler : function(){
			    		Ext.getCmp('win').close();
			    	}
			    }]
			});
			win.show();
    },
	getProdRelationStore: function(condition){
		var me = this;
		var grid = Ext.getCmp('grid');
		grid.store.removeAll(false);
		me.BaseUtil.getActiveTab().setLoading(true);//loading...
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + "common/singleGridPanel.action",
        	params: {
        		caller: "ProdRelation",
        		condition: condition
        	},
        	method : 'post',
        	callback : function(options,success,response){
        		me.BaseUtil.getActiveTab().setLoading(false);
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo){
        			showError(res.exceptionInfo);return;
        		}
        		var data = [];
        		if(!res.data || res.data.length == 2){
        			me.GridUtil.add10EmptyItems(grid);
        		} else {
        			data = Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']'));
        			if(data.length > 0){
            			grid.store.loadData(data);
            		}
        		}
        	}
        });
	},
	setLoading : function(b) {
		var mask = this.mask;
		if (!mask) {
			this.mask = mask = new Ext.LoadMask(Ext.getBody(), {
				msg : "处理中,请稍后...",
				msgCls : 'z-index:10000;'
			});
		}
		if (b)
			mask.show();
		else
			mask.hide();
	},
	getMixedGroups: function(items, fields) {
        var data = new Object(),
        k,
        o;
        Ext.each(items,
        function(d) {
            k = '';
            o = new Object();
           if(d.get(fields) != null && d.get(fields) != ''){
            k += fields + ':' + d.get(fields) + ',';
            o[fields] = d.get(fields);            
            if (k.length > 0) {
                if (!data[k]) {
                    data[k] = {
                        keys: o,
                        groups: [d]
                    };
                } else {
                    data[k].groups.push(d);
                }
             }
           }
        });
        return Ext.Object.getValues(data);
    }
});