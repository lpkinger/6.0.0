Ext.define('erp.view.hr.wage.conf.FormulaTrigger', {
	extend : 'Ext.form.field.Trigger',
	alias : 'widget.formulaTrigger',
	editable :false,
	triggerCls: 'x-form-search-trigger',
	afterrender : function() {
		this.addEvent({
					'aftertrigger' : true
				});
	},
	onTriggerClick : function(e) {
		var me = this;
		me.showFormluaWin();
	},
	showFormluaWin : function(){
		var me = this;
		var win = Ext.create('Ext.window.Window', {
			title : '自定义公式',
			closeAction: 'destroy',
			width : 640,
			items : [me.getFormulaForm()],
			buttonAlign : 'center',
			buttons : [{
				text : $I18N.common.button.erpConfirmButton,
				height : 26,
				handler : function(b) {
					var w = b.ownerCt.ownerCt,
				    items = Ext.getCmp('displayframe').items.items,
						value = '', display = '', c, h;
					Ext.Array.each(items, function(item){
						if(item.data) {
							value +=  item.data.value;
							display += item.text;
						} else {
							if(item.text == 'sysdate'){
								value += 'sysdate';
								display += 'new Date()';
							} else if(item.text == '>') {
								value += ' > ';
								display += '>';
							} else if(item.text == '<') {
								value += ' < ';
								display += '<';
							} else if(item.text == '≥') {
								value += ' >= ';
								display += '>=';
							} else if(item.text == '≤') {
								value += ' <= ';
								display += '<=';
							}else if(item.text == '并且') {
								value += ' and ';
								display += '并且';
							}  else {
								if(h && item.text == '=') {
									display += '==';
								} else
									display += item.text;
								value += item.text;
							}
						}
					});
					//主表配置
					if(me.name=='WC_TAXINEXPRESSIONTEXT'){
						me.setValue(display);
						Ext.getCmp('WC_TAXINEXPRESSION').setValue(value);
					}
					//加班配置
					if(me.name=='WO_EXPRESSIONTEXT'){
						var grid = Ext.getCmp('overworkgrid');
						var record = grid.getSelectionModel().getLastSelected();
						record.set('WO_EXPRESSIONTEXT',display);
						record.set('WO_EXPRESSION',value);
					}
					//缺勤配置
					if(me.name=='WAC_CONDEXPRESSIONTEXT'){
						var grid = Ext.getCmp('absencegrid');
						var record = grid.getSelectionModel().getLastSelected();
						record.set('WAC_CONDEXPRESSIONTEXT',display);
						record.set('WAC_CONDEXPRESSION',value);
					}
					if(me.name=='WAC_EXPRESSIONTEXT'){
						var grid = Ext.getCmp('absencegrid');
						var record = grid.getSelectionModel().getLastSelected();
						record.set('WAC_EXPRESSIONTEXT',display);
						record.set('WAC_EXPRESSION',value);
					}
					w.close();
					
				}
			}, {
				text : $I18N.common.button.erpCloseButton,
				height : 26,
				handler : function(b) {
					var w = b.ownerCt.ownerCt;
					w.close();
				}
			}]
		});
		win.show();
	},
	getFormulaForm: function(source, oldData) {
		var me = this;
		var defItems = [], defBtns = "987654321.0/*(-)+=><≥≤".split(""),
			colItems = [], moreItems = [], me = this, formula = me.formula();
		defItems.push({
			text: '←', 
			tooltip: '删除', 
			handler: function(btn) {
				formula.del(btn);
			}
		});
		defItems.push({
			text: '→', 
			tooltip: '回退', 
			handler: function(btn) {
				formula.back(btn);
			}
		});
		defItems.push({text: ','});
		defItems.push({
			text: 'RE', 
			tooltip: '重置', 
			handler: function(btn) {
				formula.reset(btn);
			}
		});
		defItems.push({
			text: 'CE', 
			tooltip: '清除', 
			handler: function(btn) {
				formula.clear(btn);
			}
		});
		Ext.Array.each(defBtns, function(b){
			var o = {text: b};
			if(b == '0')
				o.width = 108;
			defItems.push(o);
		});
		// case when
		Ext.Array.each('并且'.split(','), function(b){
			var o = {text: b, tooltip: '判断语句case when..then..when..then..else..and'};
			defItems.push(o);
		});

		colItems.push({
			text:'获取更多字段',
			columnWidth:1,
			handler:function(btn){
				var f=btn.up('form');
				btn.hide();
				f.items.items[1].items.items[1].add(f.moreItems);	
				formula.add(f.moreItems);
			}
		});
		
		var owData = [{
						columnWidth:0.5,
						height:30,
						margin:"3 3 3 3",
						text:"月薪标准",
						data:{
							value:'v_monthWageStandard'
						}
					},{
						columnWidth:0.5,
						height:30,
						margin:"3 3 3 3",
						text:"月平均工作天数",
						data:{
							value:'v_monthworkDays'
						}
					},{
						columnWidth:0.5,
						height:30,
						margin:"3 3 3 3",
						text:"工作日加班天数",
						data:{
							value:'v_workDayOvertimeDays'
						}
					},{
						columnWidth:0.5,
						height:30,
						margin:"3 3 3 3",
						text:"双休日日加班天数",
						data:{
							value:'v_weekendOvertimeDays'
						}
					},{
						columnWidth:0.5,
						height:30,
						margin:"3 3 3 3",
						text:"节假日日加班天数",
						data:{
							value:'v_holidayOvertimeDays'
						}
					},{
						columnWidth:0.5,
						height:30,
						margin:"3 3 3 3",
						text:"应勤天数",
						data:{
							value:'v_shouldattendDays'
						}
					}];
		
		var abData=[{
						columnWidth:0.5,
						height:30,
						margin:"3 3 3 3",
						text:"月薪标准",
						data:{
							value:'v_monthWageStandard'
						}
					},{
						columnWidth:0.5,
						height:30,
						margin:"3 3 3 3",
						text:"月平均工作天数",
						data:{
							value:'v_monthworkDays'
						}
					},{
						columnWidth:0.5,
						height:30,
						margin:"3 3 3 3",
						text:"事假天数",
						data:{
							value:'v_personleaveDays'
						}
					},{
						columnWidth:0.5,
						height:30,
						margin:"3 3 3 3",
						text:"病假天数",
						data:{
							value:'v_sickleaveDays'
						}
					},{
						columnWidth:0.5,
						height:30,
						margin:"3 3 3 3",
						text:"产假天数",
						data:{
							value:'v_maternityleaveDays'
						}
					},{
						columnWidth:0.5,
						height:30,
						margin:"3 3 3 3",
						text:"其他假天数",
						data:{
							value:'v_otherleaveDays'
						}
					},{
						columnWidth:0.5,
						height:30,
						margin:"3 3 3 3",
						text:"旷工天数",
						data:{
							value:'v_absentDays'
						}
					},{
						columnWidth:0.5,
						height:30,
						margin:"3 3 3 3",
						text:"应勤天数",
						data:{
							value:'v_shouldattendDays'
						}
					}];
		var taxData =[
					{
						columnWidth:0.5,
						height:30,
						margin:"3 3 3 3",
						text:"基本工资",
						data:{
							value:'wr_baseWage'
						}
					},
					{
						columnWidth:0.5,
						height:30,
						margin:"3 3 3 3",
						text:"月度绩效",
						data:{
							value:'wr_monthPerfBonus'
						}
					},
					{
						columnWidth:0.5,
						height:30,
						margin:"3 3 3 3",
						text:"加班工资",
						data:{
							value:'wr_overWorkWage'
						}
					},
					{
						columnWidth:0.5,
						height:30,
						margin:"3 3 3 3",
						text:"缺勤",
						data:{
							value:'wr_absence'
						}
					},	
					{
						columnWidth:0.5,
						height:30,
						margin:"3 3 3 3",
						text:"提成",
						data:{
							value:'wr_deductionBonus'
						}
					},
					{
						columnWidth:0.5,
						height:30,
						margin:"3 3 3 3",
						text:"应发合计",
						data:{
							value:'wr_shouldPayTotal'
						}
					},
/*					{
						columnWidth:0.5,
						height:30,
						margin:"3 3 3 3",
						text:"应扣合计",
						data:{
							value:'wr_shouldDetainTotal'
						}
					},*/
					{
						columnWidth:0.5,
						height:30,
						margin:"3 3 3 3",
						text:"补款合计",
						data:{
							value:'wr_otherSubsidyTotal'
						}
					}]
		Ext.Ajax.request({
			url:basePath + 'hr/wage/getWageItems.action',
			method : 'get',
			async:false,
			callback:function(records, options, response){
				var rs = Ext.decode(response.responseText);
				if (rs.success) {
					for (var i = 0; i < rs.data.length; i++) {
						taxData.push({
							columnWidth:0.5,
							height:30,
							margin:"3 3 3 3",
							text:rs.data[i].WI_NAME,
							data:{
								value:rs.data[i].WI_FIELDNAME
							}						
						})
					}
					
				}
			}
	 	});			
					
		
		var buttonData=[];			
					
		//所得税配置
		if(me.name=='WC_TAXINEXPRESSIONTEXT'){
			buttonData=taxData;
		}
		//加班配置
		if(me.name=='WO_EXPRESSIONTEXT'){
			buttonData=owData;
		}
		//缺勤配置
		if(me.name=='WAC_CONDEXPRESSIONTEXT' || me.name=='WAC_EXPRESSIONTEXT'){
			buttonData=abData;
		}
		
		var form = Ext.create('Ext.form.Panel', {
			bodyStyle : 'background:#f1f2f5;padding:5px',
			layout: 'vbox',
			height:510,
			items: [{
				xtype: 'fieldcontainer',
				margin: '0 3 8 3',
				width: '100%',
				id:'displayframe',
				width:600,
				height:40,
				cls: 'x-form-text x-screen',
				defaultType: 'button',
				defaults: {
					margin: '0 0 3 0',
					cls: 'x-btn-clear'
				}
			},{
				xtype: 'container',
				layout: 'hbox',
				width: '100%',
				defaultType: 'fieldcontainer',
				defaults: {flex: 1},
				items: [{
					defaultType: 'button',
					defaults: {
						width: 51,
						height: 30,
						margin: '3 3 3 3'
					},
					items: defItems
				},{
					layout: 'column',
					defaultType: 'button',
					height:420,
					autoScroll:true,
					defaults: {
						columnWidth: 0.5,
						height: 30,
						margin: '3 3 3 3'
					},
					items: buttonData
				}]
			}]
		});
		
		var btns = form.query('button');
		Ext.Array.each(btns, function(btn){
			if(!btn.handler) {
				btn.handler = function() {
					formula.add(btn);
				};
			}
		});
		if(oldData) {
			var container = form.down('fieldcontainer[cls~=x-screen]'), 
				items = me.getItemsFromFormula(source, oldData);
			container.initItems = items;
			container.add(items);
		}
		return form;
	},
	formula: function() {
		var me = this;
		me.formula_operator = [];
		return {
			log: function(oper, text, data, isfn) {
				me.formula_operator.push({oper: oper, text: text, data: data, isfn: isfn});
			},
			getContainer: function(scope) {
//				return scope.up('form').down('fieldcontainer[cls~=x-screen]');
				return Ext.getCmp('displayframe');
			},
			add: function(scope, parentScope) {
				var f = this.getContainer(parentScope || scope);
				f.add({text: scope.text, data: scope.data, isfn: scope.isfn});
				if(scope.isfn) {
					f.add({text: '('});
					this.log(1, scope.text, null, true);
					this.log(2, '(');
				} else {
					this.log(1, scope.text, scope.data);
				}
			},
			del: function(scope) {
				var f = this.getContainer(scope), l = f.down('button:last');
				if (l) {
					f.remove(l);
					this.log(0, l.text, l.data, l.isfn);
				}
			},
			back: function(scope) {
				var f = this.getContainer(scope), len = me.formula_operator.length;
				if(len > 0) {
					var i = len - 1, o = me.formula_operator[i], oper = o.oper;
					switch(oper) {
					case 0:
						f.add({text: o.text, data: o.data, isfn: o.isfn});
						break;
					case 1:
						var b = f.down('button:last');
						if(b && b.text == o.text)
							f.remove(b);
						break;
					case 2:
						var b = f.down('button:last');
						if(b && b.text == o.text) {
							f.remove(b);
							f.remove(f.down('button:last'));
						}
						break;
					case 3:
						var j = 0;
						for(;i > 0;i-- ) {
							if(me.formula_operator[i].oper == 3) {
								j = i;
							} else {
								break;
							}
						}
						for(;j < len;j++ ) {
							o = me.formula_operator[j];
							f.add({text: o.text, data: o.data, isfn: o.isfn});
						}
						i++;
						break;
					}
					me.formula_operator.splice(i);
				}
			},
			clear: function(scope) {
				var m = this, f = m.getContainer(scope), btns = f.query('button');
				f.removeAll();
				Ext.Array.each(btns, function(b){
					m.log(3, b.text, b.data, b.isfn);
				});
			},
			reset: function(scope, source, oldData) {
				var f = this.getContainer(scope);
				if(me.formula_operator.length > 0) {
					f.removeAll();
					f.add(f.initItems);
				}
				me.formula_operator = [];
			}
		};
	},
	getItemsFromFormula: function(source, oldData) {
		var sign = /[\+\-\*=\/%,\(\)\s]/, units = oldData._split(sign), items = [],
//			fns = ['abs', 'ceil', 'floor', 'round', 'nvl', 'nvl2', 'lpad', 'rpad', 'trim', 'trunc', 'to_char', 'add_months'], 
			cw = ['case', 'when', 'then', 'else','and', 'sysdate', '||'];
		Ext.Array.each(units, function(unit){
			if(isNumber(unit)) {
				Ext.Array.each(unit.split(""), function(u){
					items.push({text: u});
				});
			} else if(fns.indexOf(unit) > -1){
				items.push({
					text: unit,
					isfn: true
				});
			} else if(sign.test(unit) || cw.indexOf(unit) > -1) {
				if(unit != ' ')
					items.push({text: unit});
			} else {
				var table = null, field = unit, type = null;
				if(unit.indexOf('.') > 0) {
	    			table = unit.substring(0, unit.indexOf('.'));
	    			field = unit.substr(unit.indexOf('.')+1);
	    		}
				var res = source.queryBy(function(record){
	    			return record.get('stg_table') == table && record.get('stg_field') == field;
	    		}), item = res.first();
	    		if (item) {
	    			unit = item.get('stg_text');
	    			type = item.get('stg_type');
	    		}
				items.push({
					text: unit,
					data: {stg_table: table, stg_field: field, stg_type: type}
				});
			}
		});
		return items;
	}
});