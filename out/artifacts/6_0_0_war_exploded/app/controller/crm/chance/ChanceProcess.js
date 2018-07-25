Ext.QuickTips.init();
Ext.define('erp.controller.crm.chance.ChanceProcess', {
	extend: 'Ext.app.Controller',
	FormUtil: Ext.create('erp.util.FormUtil'),
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	views:['crm.chance.ChanceProcess','crm.chance.ProcessGrid','core.form.ConDateField','core.form.MultiField','core.trigger.DbfindTrigger'],
	init:function(){
		var me = this;
		this.control({
			'button[id=query]': {
				afterrender: function(btn) {
					setTimeout(function(){
						me.showFilterPanel(btn);
					}, 200);
				},
				click: function(btn) {
					me.showFilterPanel(btn);
				}
			},
			'button[name=export]': {
    			click: function() {
    				var grid = Ext.getCmp('processgrid');
    				me.BaseUtil.exportGrid(grid, '商机进度');
    			}
    		}
		});
	},
	showFilterPanel: function(btn) {
		var filter = Ext.getCmp(btn.getId() + '-filter');
		if(!filter) {
			filter = this.createFilterPanel(btn);
		}
		filter.show();
	},
	hideFilterPanel: function(btn) {
		var filter = Ext.getCmp(btn.getId() + '-filter');
		if(filter) {
			filter.hide();
		}
	},
	getCondition: function(pl) {
		var condition="1=1";
		Ext.each(pl.items.items, function(f){
			if(f.name != null && f.name != ''){
				if((f.xtype == 'checkbox' || f.xtype == 'radio')){
					if(f.value == true) {
						if(condition == ''){
							condition += f.name;
						} else {
							condition += ' AND ' + f.name;
						}
					}
				} else if(f.xtype == 'datefield' && f.value != null && f.value != '' && !contains(f.name, 'to:', true)){
					var v = Ext.Date.format(new Date(f.value), 'Y-m-d');
					if(condition == ''){
						condition += f.name + "=to_date('" + v + "', 'yyyy-MM-dd')";
					} else {
						condition += ' AND ' + f.name + "=to_date('" + v + "', 'yyyy-MM-dd')";
					}
				} else if(f.xtype == 'datetimefield' && f.value != null){

					var v = Ext.Date.format(new Date(f.value), 'Y-m-d H:i:s');
					if(condition == ''){
						condition += f.name + "=to_date('" + v + "', 'yyyy-MM-dd HH24:mi:ss')";
					} else {
						condition += ' AND ' + f.name + "=to_date('" + v + "', 'yyyy-MM-dd HH24:mi:ss')";
					}
				} else if(f.xtype == 'numberfield' && f.value != null && f.value != '' && !contains(f.name, 'to:', true)){
					var endChar = f.name.substr(f.name.length - 1);
					if(endChar != '>' && endChar != '<')
						endChar = '=';
					else
						endChar = '';
					if(condition == ''){
						condition += f.name + endChar + f.value;
					} else {
						condition += ' AND ' + f.name + endChar + f.value;
					}
				} else if(f.xtype == 'combo' && f.value == '$ALL'){
					if(f.store.data.length > 1) {
						if(condition == ''){
							condition += '(';
						} else {
							condition += ' AND (';
						}
						var _a = '';
						f.store.each(function(d, idx){
							if(d.data.value != '$ALL') {
								if(_a == ''){
									_a += f.name + "='" + d.data.value + "'";
								} else {
									_a += ' OR ' + f.name + "='" + d.data.value + "'";
								}
							}
						});
						condition += _a + ')';
					}
				} else if(f.xtype=='adddbfindtrigger' && f.value != null && f.value != ''){
					if(condition == ''){
						condition += f.name + ' in (' ;		
					} else {
						condition += ' AND ' + f.name + ' in (';
					}
					var str=f.value,constr="";
					for(var i=0;i<str.split("#").length;i++){
						if(i<str.split("#").length-1){
							constr+="'"+str.split("#")[i]+"',";
						}else constr+="'"+str.split("#")[i]+"'";
					}
					condition +=constr+")";
				} else {			
					if(contains(f.name, 'to:', true)){
						if(!grid.toField){
							grid.toField = new Array();
						}
						grid.toField.push(f.name.split(':')[1]);
					} else {
						if(!Ext.isEmpty(f.value)){
							if(contains(f.value.toString(), 'BETWEEN', true) && contains(f.value.toString(), 'AND', true)){
								if(condition == ''){
									condition += f.name + " " + f.value;
								} else {
									condition += ' AND (' + f.name + " " + f.value + ")";
								}
							} else if(contains(f.value.toString(), '||', true)){
								var str = '';
								Ext.each(f.value.split('||'), function(v){
									if(v != null && v != ''){
										if(str == ''){
											str += f.name + "='" + v + "'";
										} else {
											str += ' OR ' + f.name + "='" + v + "'";
										}
									}
								});
								if(condition == ''){
									condition += "(" + str + ")";
								} else {
									condition += ' AND (' + str + ")";
								}
							} else if(f.value.toString().charAt(0) == '!'){ 
								if(condition == ''){
									condition += 'nvl(' + f.name + ",' ')<>'" + f.value.substr(1) + "'";
								} else {
									condition += ' AND (nvl(' + f.name + ",' ')<>'" + f.value.substr(1) + "')";
								}
							} else {
								if(f.value.toString().indexOf('%') >= 0) {
									if(condition == ''){
										condition += f.name + " like '" + f.value + "'";
									} else {
										condition += ' AND (' + f.name + " like '" + f.value + "')";
									}
								} else {
									if(condition == ''){
										condition += f.name + "='" + f.value + "'";
									} else {
										condition += ' AND (' + f.name + "='" + f.value + "')";
									}
								}
							}
						}
					}
				}
			}
		});
		return condition;
	},
	createFilterPanel: function(btn) {
		var me = this;
		var filter = Ext.create('Ext.Window', {
			id: btn.getId() + '-filter',
			style: 'background:#f1f1f1',
			title: '筛选条件',
			width: 500,
			height: 415,
			layout: 'column',
			defaults: {
				margin: '2 2 2 10'
			},
			modal:true,
			items: [{
				xtype:'condatefield',
				id:'bc_recorddate',
				name:'bc_recorddate',
				columnWidth:1,
				fieldLabel:'商机日期'
			},{
				xtype: 'multifield',
				id: 'bc_custcode',
				name: 'bc_custcode',
				secondname:'bc_custname',
				columnWidth: 1,
				fieldLabel:'商机客户'
			},{
				xtype: 'multifield',
				id: 'bc_departmentcode',
				name: 'bc_departmentcode',
				secondname:'bc_department',
				columnWidth: 1,
				fieldLabel:'部门'
			},{
				xtype: 'combo',
				id: 'bc_currentprocess',
				name: 'bc_currentprocess',
				columnWidth: .5,
				fieldLabel:'商机阶段',
				queryMode: 'local',
				displayField: 'display',
				valueField: 'value',
				editable: false,
				store: Ext.create('Ext.data.Store', {
					fields: ['display', 'value'],
					data : [ ]
				}),
				listeners: {
					afterrender: function(f) {
						me.getStages(f);
					}
				}
			}],
			buttonAlign: 'center',
			buttons: [{
				text: '确定',
				width: 60,
				cls: 'x-btn-blue',
				handler: function(btn) {
					var fl = btn.ownerCt.ownerCt,
					con = me.getCondition(fl);
					me.query(con);
					fl.hide();
				}
			},{
				text: '关闭',
				width: 60,
				cls: 'x-btn-blue',
				handler: function(btn) {
					var fl = btn.ownerCt.ownerCt;
					fl.hide();
				}
			}]
		});
		return filter;
	},
	query: function(con) {
		var grid = Ext.getCmp('processgrid');
		grid.loadNewData(grid,con);
	},
	getStages: function(f) {
		Ext.Ajax.request({
			url : basePath + 'common/getFieldDatas.action',
			async: false,
			params: {
				caller: 'BusinessChanceStage',
				field: 'bs_name',
				condition: '1=1 order by bs_detno asc'
			},
			method : 'post',
			callback : function(options,success,response){
				var rs = new Ext.decode(response.responseText);
				if(rs.exceptionInfo){
					showError(rs.exceptionInfo);return null;
				}
				if(rs.success && rs.data){
					var cr = rs.data.split('#'),c = new Array();
					Ext.each(cr, function(r){
						c.push({display: r, value: r});
					});
					f.store.add(c);
				}
			}
		});
	}
});