/**
 * 通用查询按钮
 */
Ext.define('erp.view.core.button.Commonquery', {
	extend : 'Ext.Button',
	alias : 'widget.erpCommonqueryButton',
	iconCls : 'x-button-icon-query',
	cls : 'x-btn-gray',
	style : {
		marginLeft : '10px'
	},
	width : 90,
	initComponent : function(){ 
		this.callParent(arguments); 
		this.addEvents({
			base: true,
			formal: true
		});
	},
	listeners: {
		afterrender: function(btn) {
			var me = this;
			var type = me.xtype+'!'+me.id;
			Ext.Ajax.request({
				url: basePath + 'common/getqueryConfigs.action',
				params: {
					caller : caller,
					xtype : type
				},
				callback: function(opt, s, r) {
					var rs = Ext.decode(r.responseText);
					if(rs.exceptionInfo) {
						showError(rs.exceptionInfo);
					} else {
						if(rs.log){
							me.setText(rs.log.buttondesc);
							me.setWidth(rs.log.buttonlength);
							me.querycaller = rs.log.querycaller ;
							me.fixedcondition = rs.log.fixedcondition;
							me.fieldcondition = rs.log.fieldcondition;
							me.type = rs.log.type;
							me.winheight = rs.log.winheight;
							me.setDisabled(false);
							//异步读取后 让toolbar重新布局
							var t = Ext.getCmp('form_toolbar');
							t.doLayout();
						}						
					}
				}
			});
		}
	},
	getWin:function(c){
		var me = this,record = '',condition  = '1=1',title='';
		if(me.type=='main'){
			if(me.fieldcondition){
				field = me.fieldcondition.split('[')[1].split(']')[0];
				title = Ext.getCmp(''+field+'').value;
				condition = me.fieldcondition.split('=')[0] + '=' + '\'' + title + '\'';
			}
		} else if(me.type=='detail'){
			var grid = Ext.getCmp('grid');
			if(grid){
				record = grid.selModel.lastSelected ;
				if(!record){
					showError('请先选择明细行!');
				}
			}else{
				record = c;
			}
			if(me.fieldcondition){
				field = me.fieldcondition.split('[')[1].split(']')[0];
				title = record.data[''+field+''];
				condition = me.fieldcondition.split('=')[0] + '=' + '\'' + record.data[''+field+''] + '\'';
			}
		}
		if(me.fixedcondition){
			condition = condition + ' and ' + me.fixedcondition;
		}
		var win = Ext.getCmp(''+me.id+'_win');
		var height = me.winheight == null ? '100%': me.winheight;
		if(win == null){
			win = Ext.create('Ext.window.Window', {
				width: '80%',
				height: height,
				title:title,
				maximizable : true,
				buttonAlign : 'center',
				layout: 'anchor',
				closeAction: 'destory',
			});
			win.show();
			var gridid = win.id+'_grid';
			win.add(Ext.create('erp.view.core.grid.Panel5', {
				id: gridid,
				anchor: '100% 100%',
				caller: me.querycaller,
				condition: condition,
				layout : 'fit',
				bbar: null,
				closeAction: 'destory'
			}));
		} else {
			win.show();
		}
	},
	handler: function() {
		var me = this;
		me.getWin();
	}
});