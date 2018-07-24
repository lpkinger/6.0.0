/**
 * 选择员工TreeGrid
 * <br>
 * FormDetail表fd_dbfind配置ET
 * DetailGrid表dg_dbbutton配置-4
 */
Ext.define('erp.view.core.trigger.EmpTrigger', {
	extend : 'Ext.form.field.Trigger',
	alias : 'widget.emptrigger',
	triggerCls : 'x-form-search-trigger',
	initComponent : function() {
		this.addEvents({
			aftertrigger : true,
			beforetrigger : true
		});
		this.callParent(arguments);
	},
	listeners : {
		focus : function(f) {
			if (!f.readOnly) {
				var trigger = this;
				trigger.lastTriggerId = trigger.id;
				if (!trigger.ownerCt && !trigger.owner) {
					var grid = Ext.ComponentQuery.query('gridpanel');
					Ext.Array.each(grid, function(g, index) {
						Ext.Array.each(g.columns, function(column, index1) {
							if (column.dataIndex == trigger.name) {
								dbfind = column.dbfind;
								trigger.owner = g;
							}
						});
					});
				}
			}
		}
	},
	onTriggerClick : function() {
		var trigger = this;
		if (!trigger.ownerCt) {
			if (trigger.owner.selModel) {
				trigger.record = trigger.owner.selModel.selected.items[0];
			}
		}
		this.showWin();
	},
	showWin : function() {
		this.fireEvent('beforetrigger', this);
		var win = Ext.getCmp('tri-emp-win');
		if (!win) {
			var me = this, url = basePath + 'jsps/common/empdbfind.jsp?tid=' + this.id;
			win = Ext.create('Ext.Window', {
				id : 'tri-emp-win',
				title : '员工资料',
				height : "100%",
				width : "80%",
				maximizable : true,
				buttonAlign : 'center',
				layout : 'anchor',
				modal : true,
				closeAction: 'hide',
				items: [{
					tag : 'iframe',
					frame : true,
					anchor : '100% 100%',
					layout : 'fit',
					html : '<iframe src="' + url + '" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
				}],
				buttons : [{
					text : $I18N.common.button.erpConfirmButton,
					iconCls: 'x-button-icon-save',
					cls: 'x-btn-gray',
					handler: function(btn) {
						me.onConfirm();
					}
				},{
					text : $I18N.common.button.erpCloseButton,
					iconCls: 'x-button-icon-close',
					cls: 'x-btn-gray',
					handler : function(btn){
						btn.ownerCt.ownerCt.hide();
					}
				}]
			});
		}
		win.show();
	},
	onConfirm: function() {
		var me = this, contentwindow = Ext.getCmp('tri-emp-win').body.dom.getElementsByTagName('iframe')[0].contentWindow;
   		var tree = contentwindow.Ext.getCmp('emptreegrid');
   		var datas = tree.getChecked(), record = this.record, trigger = this, grid = this.owner;
   		if(this.ownerCt) {
   			me.fireEvent('aftertrigger', me, datas);
   			this.fireEvent('aftertrigger',this,datas);
   		} else {
   			var dbfinds = this.owner.dbfinds;
   			if(dbfinds != null){
   				var keys = Ext.Object.getKeys(datas[0].raw.data);
   				Ext.each(datas, function(d, i){
   					if(i > 0){
   						record = trigger.next(grid, record);
   					}
   					if(record && d && d.raw) {
   						Ext.each(dbfinds, function(dbfind, index){
   		    				if(Ext.isEmpty(dbfind.trigger) || dbfind.trigger == trigger.name) {
   		    					var ss = dbfind.dbGridField.split(';');
   			    				for(var i in ss) {
   			    					if(Ext.Array.contains(keys, ss[i])) {
   				    					record.set(dbfind.field, d.raw.data[ss[i]]);
   				    				}
   			    				}
   		    				}
   		    			});
   					}
   				});
   			}
   		}
		Ext.getCmp('tri-emp-win').hide();
	},
	/**
	    * 递归grid的下一条
	    */
	   next: function(grid, record){
			record = record || grid.selModel.lastSelected;
			if(record){
				//递归查找下一条，并取到数据
				var d = grid.store.getAt(record.index + 1);
				if(d){
					return d;
				} else {
					if(record.index + 1 < grid.store.data.items.length){
						this.next(grid, d);
					}
				}
			}
		}
});