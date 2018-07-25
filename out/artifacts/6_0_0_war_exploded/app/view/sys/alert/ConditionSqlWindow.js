Ext.define('erp.view.sys.alert.ConditionSqlWindow', {
	extend: 'Ext.window.Window',
	alias: 'widget.conditionsqlwin',
	id: 'conditionSqlWin',
	title: '条件设置',
	modal: true,
	width: 600,
	height: '70%',
	autoScroll: true,
	initComponent : function(){
		var me = this;
		var assignGrid = Ext.getCmp('grid');
		this.initConfig = assignGrid.getStore().getAt(me.storeIndex-1).get('aia_conditionconfig');
		Ext.apply(me, {
			items: [{
				xtype:'panel',
				cls:'clearconditionpanel',
				items:[{
					xtype:'button',
					text:'清空条件',
					iconCls:'clearconditionspico',
					cls:'clearconditionsp',
					id:'clearconditionsp',
					width:90,
					listeners:{
						click:function(){
							var conpanel=this.ownerCt.ownerCt.down('form');
							conpanel.removeAll();
						}
					}
				}]
			}, {
				xtype: 'form',
				id: 'conditionSqlForm',
				bodyPadding: 5,
				autoScroll: true
			}, {
				xtype:'panel',
				items:[{
					xtype:'button',
					text:'新增行',
					width:70,				
					iconCls:'newaddpanelico',
				    cls:'newaddpanel',
				    id:'newaddpanel',
					handler:function(){
						me.insertCondition();
					}	
				}]
			}],
			buttonAlign: 'center',
			buttons: [{
				xtype: 'button',
				text: '确定',
				handler: function() {
					var conditionSqlWin = Ext.getCmp('conditionSqlWin'),
						form = Ext.getCmp('conditionSqlForm'),
						formValues = form.getForm().getValues(),
						conditionItems = form.items.items,
						/*formValue = form.getForm().getValues(),*/
						assignGrid = Ext.getCmp('grid'),
						store = assignGrid.getStore(),
						rowStore = store.getAt(Number(conditionSqlWin.storeIndex)-1),
						conditionConfig = [],
						conditionSql = '';
					
					if(Ext.getCmp('aii_statuscode').value!='ENTERING') {
						conditionSqlWin.close();
						return;
					}
					if(conditionItems.length > 0) {
						for(var i=0;i<conditionItems.length;i++) {
							var conditionItem = conditionItems[i],
								item = conditionItem['fieldItem'],
								store = item.store;
							
							if(!conditionItem.items.items[0].value || !conditionItem.items.items[1].value) {
								Ext.Msg.alert('提示','请补全条件项！');
								return;
							}
							
							conditionConfig.push({
								originalxtype: conditionItem['originalxtype'],
								column_value: conditionItem.items.items[0].value,
								type: conditionItem.items.items[1].value,
								value: conditionItem['originalxtype']!='datefield'?(conditionItem.items.items.length==4?conditionItem.items.items[2].value+''||'':
									(conditionItem.items.items.length==5?
										(conditionItem.items.items[2].value+'~'+conditionItem.items.items[3].value):'')):
										(conditionItem.items.items.length==4?Ext.util.Format.date(conditionItem.items.items[2].value, 'Y-m-d')+''||'':
									(conditionItem.items.items.length==5?
										(Ext.util.Format.date(conditionItem.items.items[2].value, 'Y-m-d')+'~'+Ext.util.Format.date(conditionItem.items.items[3].value, 'Y-m-d')):''))
							});
							
							conditionSql += conditionItem.formatConditon();
							if(i!=(conditionItems.length-1))conditionSql += ' and ';
						}
					}
					var arr = conditionSql.split(' and ');
					for(var i=arr.length-1;i>=0;i--) {
						if(arr[i]=='(') {
							arr.splice(i,1);
						}
					}
					if(rowStore){
						rowStore.set('aia_condition', arr.join(' and '));
						rowStore.set('aia_conditionconfig', Ext.encode(conditionConfig));
					}
					conditionSqlWin.close();
				}
			}]
		});
		me.callParent(arguments);
	},
	insertCondition: function(conditionconfig) {
		var me = this;
		var form = Ext.getCmp('conditionSqlForm');
		conditionconfig = conditionconfig?Ext.decode(conditionconfig):
			[{originalxtype:'',conData:{column_value:'',value:''}}];
		for(var i=0;i<conditionconfig.length;i++) {
			form.add({
				xtype:'concontainer',
				FieldStore:form.FieldStore,
				originalxtype: conditionconfig[i]['originalxtype'],
		    	conData: {
					column_value: conditionconfig[i]['column_value'],
					type: conditionconfig[i]['type'],
					value: conditionconfig[i]['value']
				}
			});	
		}
	}
});