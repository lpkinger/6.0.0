Ext.QuickTips.init();
Ext.define('erp.controller.fs.credit.CreditTargets', {
	extend : 'Ext.app.Controller',
	FormUtil : Ext.create('erp.util.FormUtil'),
	GridUtil : Ext.create('erp.util.GridUtil'),
	BaseUtil : Ext.create('erp.util.BaseUtil'),
	views : ['core.form.Panel', 'fs.credit.CreditTargets', 'core.grid.Panel2','core.toolbar.Toolbar', 'core.form.MultiField',
			'core.button.TurnProject','core.button.Save', 'core.button.Add','core.button.Submit', 'core.button.Upload',
			'core.button.ResAudit','core.button.Audit','core.button.Close','core.button.Delete','core.button.Update',
			'core.button.DeleteDetail','core.button.ResSubmit', 'core.button.Export','core.trigger.DbfindTrigger',
			'core.trigger.TextAreaTrigger','core.form.YnField', 'core.grid.YnColumn', 'core.form.StatusField','core.form.FileField',
			'core.button.CopyAll','core.button.ResetSync', 'core.button.RefreshSync','core.button.ChangeResponsible',
			'core.form.MultiField','core.button.ItemsValueSet'],
	init : function() {
		var me = this;
		this.control({
			'field[name=ct_isleaf]' : {
				change : function(field, newValue, oldValue, eOpts) {				
					if(newValue==0){
						var ct_subof = Ext.getCmp('ct_subof');
						if(ct_subof){
							ct_subof.allowBlank=true;
							document.getElementById("ct_subof").childNodes[0].style ='margin-right:5px;width:100px;';				
						}
						var ct_satisfaction = Ext.getCmp('ct_satisfaction');
						if(ct_satisfaction){
							ct_satisfaction.setReadOnly(true);
							ct_satisfaction.setFieldStyle('background:#e0e0e0;color:#515151;');
							ct_satisfaction.allowBlank=true;
							document.getElementById("ct_satisfaction").childNodes[0].style ='margin-right:5px;width:100px;';
						}
						var ct_nsatisfaction = Ext.getCmp('ct_nsatisfaction');
						if(ct_nsatisfaction){
							ct_nsatisfaction.setReadOnly(true);
							ct_nsatisfaction.setFieldStyle('background:#e0e0e0;color:#515151;');
							ct_nsatisfaction.allowBlank=true;
							document.getElementById("ct_nsatisfaction").childNodes[0].style ='margin-right:5px;width:100px;';
						}
						
						var ct_standard = Ext.getCmp('ct_standard');
						if(ct_standard){
							ct_standard.setReadOnly(true);
							ct_standard.setFieldStyle('background:#e0e0e0;color:#515151;');
						}
					}else{
						var ct_subof = Ext.getCmp('ct_subof');
						if(ct_subof){
							ct_subof.allowBlank=false;			
							document.getElementById("ct_subof").childNodes[0].style ='margin-right:5px;width:100px;color:#FF0000;';										
						}
						var ct_type = Ext.getCmp('ct_type');					
						if(ct_type&&ct_type.value=='FINANCE'){
						var ct_satisfaction = Ext.getCmp('ct_satisfaction');
						if(ct_satisfaction){
							ct_satisfaction.setReadOnly(false);
							ct_satisfaction.setFieldStyle('background:#FFFAFA;color:#515151;');
							ct_satisfaction.allowBlank=false;
							document.getElementById("ct_satisfaction").childNodes[0].style ='margin-right:5px;width:100px;color:#FF0000;';
						}
						var ct_nsatisfaction = Ext.getCmp('ct_nsatisfaction');
						if(ct_nsatisfaction){
							ct_nsatisfaction.setReadOnly(false);
							ct_nsatisfaction.setFieldStyle('background:#FFFAFA;color:#515151;');
						}
						}
						var ct_standard = Ext.getCmp('ct_standard');
						if(ct_standard){
							ct_standard.setReadOnly(false);
							ct_standard.setFieldStyle('background:#FFFAFA;color:#515151;');
							ct_nsatisfaction.allowBlank=false;
							document.getElementById("ct_nsatisfaction").childNodes[0].style ='margin-right:5px;width:100px;color:#FF0000;';
						}
					}
				}
			},
			'dbfindtrigger[name=ct_subof]' : {
				afterrender : function(f) {
					var ct_isleaf = Ext.getCmp('ct_isleaf');
					if(ct_isleaf&&ct_isleaf.value==0){
						f.allowBlank=true;
						document.getElementById("ct_subof").childNodes[0].style ='margin-right:5px;width:100px;';	
					}else{
						f.allowBlank=false;
						document.getElementById("ct_subof").childNodes[0].style ='margin-right:5px;width:100px;color:#FF0000;';	
					}
				},
				beforetrigger : function(trigger){
					trigger.autoDbfind = false;
	    			trigger.dbKey='ct_type';
	    			trigger.mappingKey='ct_type';
	    			trigger.dbMessage='请先选择类型！';
				}
			},
			'field[name=ct_satisfaction]' : {
				afterrender : function(f) {
					var status = Ext.getCmp('ct_statuscode');
					var ct_isleaf = Ext.getCmp('ct_isleaf');
					var ct_type = Ext.getCmp('ct_type');
					if((ct_isleaf&&ct_isleaf.value==0)||(ct_type&&ct_type.value=='NOFINANCE')){
						if (status && status.value == 'ENTERING') {
							f.setReadOnly(true);
							f.setFieldStyle('background:#e0e0e0;color:#515151;');
						}
						f.allowBlank=true;
						document.getElementById("ct_satisfaction").childNodes[0].style ='margin-right:5px;width:100px;';
					}else{
						if (status && status.value == 'ENTERING') {
							f.setReadOnly(false);
							f.setFieldStyle('background:#FFFAFA;color:#515151;');
						}
					}
				}
			},
			'field[name=ct_nsatisfaction]' : {
				afterrender : function(f) {
					var status = Ext.getCmp('ct_statuscode');
					var ct_isleaf = Ext.getCmp('ct_isleaf');
					var ct_type = Ext.getCmp('ct_type');
					if((ct_isleaf&&ct_isleaf.value==0)||(ct_type&&ct_type.value=='NOFINANCE')){
						if (status && status.value == 'ENTERING') {
							f.setReadOnly(true);
							f.setFieldStyle('background:#e0e0e0;color:#515151;');
						}
						f.allowBlank=true;
						document.getElementById("ct_nsatisfaction").childNodes[0].style ='margin-right:5px;width:100px;';
					}else{
						if (status && status.value == 'ENTERING') {
							f.setReadOnly(false);
							f.setFieldStyle('background:#FFFAFA;color:#515151;');
						}
					}
				}
			},
			'field[name=ct_standard]' : {
				afterrender : function(f) {
					var status = Ext.getCmp('ct_statuscode');
					var ct_isleaf = Ext.getCmp('ct_isleaf');
					if(ct_isleaf&&ct_isleaf.value==0){
						f.setReadOnly(true);
						f.setFieldStyle('background:#e0e0e0;color:#515151;');
					}else{
						if (status && status.value == 'ENTERING') {
							f.setReadOnly(false);
							f.setFieldStyle('background:#FFFAFA;color:#515151;');
						}
					}
				}
			},
			'field[name=ct_sqldesc]' : {
				focus : function(f) {
					var status = Ext.getCmp('ct_statuscode');
					if (status && status.value == 'ENTERING') {
						me.onFormulaClick(f.value, function(formula,sql){
							var ct_sql = Ext.getCmp('ct_sql');
							if(sql){
								if(sql&&me.testSQL(sql)){
					    			f.setValue(formula);
					    			if(ct_sql){
					    				ct_sql.setValue(sql);
					    			}
								}
							}else{
								f.setValue(formula);
				    			if(ct_sql){
				    				ct_sql.setValue(sql);
				    			}
							}
			    		});
					}
				}
			},
			'field[name=ct_assessdesc]' : {
				focus : function(f) {
					var status = Ext.getCmp('ct_statuscode');
					var editabled = Ext.getCmp('ct_editabled');
					if (status && status.value == 'ENTERING'&&editabled&&editabled.value==0) {
						me.addScore(f.value);
					}
				}
			},
			'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('CreditTargets', '信用指标设置', 'jsps/fs/credit/creditTargets.jsp');
    			}
        	},
			'erpSaveButton': {
    			click: function(btn){
    				if(me.checkForm())
						this.FormUtil.beforeSave(this);				
    			}
        	},
			'erpDeleteButton' : {
				click : function(btn) {
					me.FormUtil.onDelete(Ext.getCmp('ct_id').value);
				}
			},
			'erpUpdateButton' : {
				click : function(btn) {
					if(me.checkForm())
					me.FormUtil.onUpdate(this);
				}
			},
			'erpCloseButton' : {
				click : function(btn) {
					me.FormUtil.beforeClose(me);
				}
			},
			'erpSubmitButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('ct_statuscode');
					if (status && status.value != 'ENTERING') {
						btn.hide();
					}
				},
				click : function(btn) {
					if(me.checkForm())
					me.FormUtil.onSubmit(Ext.getCmp('ct_id').value);
				}
			},
			'erpResSubmitButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('ct_statuscode');
					if (status && status.value != 'COMMITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onResSubmit(Ext.getCmp('ct_id').value);
				}
			},
			'erpAuditButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('ct_statuscode');
					if (status && status.value != 'COMMITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onAudit(Ext.getCmp('ct_id').value);
				}
			},
			'erpResAuditButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('ct_statuscode');
					if (status && status.value != 'AUDITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onResAudit(Ext.getCmp('ct_id').value);
				}
			},
			'erpItemsValueSetButton' : {
				afterrender : function(btn) {
					var type = Ext.getCmp('ct_type');
					var status = Ext.getCmp('ct_statuscode');
					var editabled = Ext.getCmp('ct_editabled');
					if (status && status.value != 'ENTERING'||(editabled&&editabled.value==0)) {
						btn.hide();
					}
				},
				click : function(btn) {
					me.createWindow('CreditTargetsCombo');
				}
			}
		})
	},
	checkForm:function(){
		var ct_type = Ext.getCmp('ct_type');
		if(ct_type&&ct_type.value=='FINANCE'){
			ct_isleaf = Ext.getCmp('ct_isleaf');
			if(ct_isleaf&&(Math.abs(ct_isleaf.value)==1)){
				var ct_sql = Ext.getCmp('ct_sql');
				if(!ct_sql||ct_sql.value==''){
					showError('类型为财务因素的指标，计算公式SQL不能为空！');
					return false;
				}
			}
		}
		return true;
	},
	createWindow:function(caller){
		var id = Ext.getCmp('ct_id').value;
		var url = 'jsps/fs/credit/itemsValueSet.jsp?whoami='+caller+'&gridCondition=ctc_ctidIS'+id;
		var win = new Ext.window.Window({
			id: 'win',
			title:"项目值设置",
			width : '70%',
			height : '85%',
			draggable : true,
			closable : true,
			modal : true,
			layout : 'fit',
			items: [{
		    	  tag : 'iframe',
		    	  frame : false,
		    	  layout : 'fit',
		    	  html : '<iframe src="'+ basePath + url+'" height="100%" width="100%" frameborder="0" style="border-width: 0px;padding: 0px;" scrolling="auto"></iframe>'
		    }]
		});
		win.show();
	},
	onFormulaClick: function(oldData, callback) {
		var me = this;
		var win = Ext.create('Ext.window.Window', {
			title : '计算公式',
			closeAction: 'destroy',
			height:height*0.95,
			items : [me.getFormulaForm(oldData)],
			modal : true,
			buttonAlign : 'center',
			buttons : [{
				text : $I18N.common.button.erpConfirmButton,
				handler : function(b) {
					var w = b.ownerCt.ownerCt,
						items = w.query('form > container > button'),
						text = '', sql = '',os='';
					Ext.Array.each(items, function(item){
						text +=item.text;
						if(item.data){							
							os+='NVL('+item.data+',0)';
						}else{
							os+=item.text;
						}
					});
					var unit = Ext.getCmp('ct_unit');
					if(unit&&unit.value!=''&&unit.value!='*1'){
						os='('+os+')';
						os+=unit.value;
					}
					if(os){
						sql="SELECT ROUND("+os+",2) FROM FaItems WHERE FI_YEAR='v_year'";
						sql = me.splitdesc(sql,os);	
					}
					
					callback && callback.call(me, text,sql);
					w.close();
				}
			}, {
				text : $I18N.common.button.erpCloseButton,
				handler : function(b) {
					var w = b.ownerCt.ownerCt;
					w.close();
				}
			}]
		});
		win.show();
	},
	getFormulaForm: function(oldData) {
		var defItems = [], defBtns = "789/%456*(123-)0.+=><≥≤≠".split(""),
		me = this, formula = me.formula();
		if(!me.colItems||me.colItems.length<1){
			Ext.Ajax.request({
				url: basePath + 'fs/credit/getColItems.action',
				method: 'GET',
				async: false,
				callback: function(opt, s, res){
					var res = Ext.decode(res.responseText);
					if(res.success){
						me.colItems = res.data;
					}else if(res.exceptionInfo){
						showError(res.exceptionInfo);
					}
				}
			});
		}
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
				o.width = 88;
			defItems.push(o);
		});
		defItems.push({text: 'nvl', isfn: true, tooltip: 'nvl(x,y)，如果x不为空，返回x，否则返回y'});
		defItems.push({text: 'round', isfn: true, tooltip: 'round(x,y)，返回四舍五入到小数点右边y位的x值'});
		defItems.push({text: 'floor', isfn: true, tooltip: 'floor(x)，返回小于或等于x的最大整数'});
		defItems.push({text: 'ceil', isfn: true, tooltip: 'ceil(x)，返回大于或等于x的最小整数'});
		defItems.push({text: 'abs', isfn: true, tooltip: 'abs(x)，返回x的绝对值'});
		defItems.push({text: 'nvl2', isfn: true, tooltip: 'nvl2(x,y,z)，如果x不为空，返回y，否则返回z'});
		defItems.push({text: 'trim', isfn: true, tooltip: 'trim(x)，去除x前后空格'});
		defItems.push({text: 'lpad', isfn: true, tooltip: 'lpad(x,y,z)，如果x的长度小于y，左边填充z'});
		defItems.push({text: 'rpad', isfn: true, tooltip: 'rpad(x,y,z)，如果x的长度小于y，右边填充z'});
		defItems.push({text: '‖', tooltip: '字符串连接符'});
		defItems.push({text: 'sysdate', tooltip: 'sysdate，当前时间', width: 88});
		defItems.push({text: 'to_char', isfn: true, tooltip: 'to_char(x,y)，日期x按格式y转化成字符串', width: 88});
		defItems.push({text: 'trunc', isfn: true, tooltip: 'trunc(x,y)，日期截断。清除时分秒：trunc(sysdate)；年初：trunc(sysdate,\'y\')；月初：trunc(sysdate,\'mm\')'});
		// case when
		Ext.Array.each('case,when,then,else,end'.split(','), function(b){
			var o = {text: b, tooltip: '判断语句case when..then..when..then..else..end'};
			defItems.push(o);
		});
		defItems.push({
			width: 229,
			text: '添加自定义内容', 
			handler: function(btn) {
				me.onUserDefinedClick(function(text){
					formula.add({text: "'" + text + "'"}, btn);// 当字符串处理
				});
			}
		});

		var form = Ext.create('Ext.form.Panel', {
			bodyStyle : 'background:#f1f2f5;padding:5px',
			width: 900,
			items: [{
				xtype: 'container',
				margin: '0 3 8 3',
				width: '100%',
				autoScroll : true,
				height: 100,
				cls: 'x-form-text x-screen',
				defaultType: 'button',
				defaults: {
					margin: '0 0 3 0',
					cls: 'x-btn-clear'
				}
			},{
				xtype: 'container',
				layout: 'hbox',
				defaultType: 'container',
				items: [{
					defaultType: 'button',
					flex: 1,
					defaults: {
						width: 41,
						height: 30,
						margin: '3 3 3 3'
					},
					items: defItems
				},{
					layout: 'column',
					defaultType: 'button',
					autoScroll : true,
					height: height*0.9-145,
					flex: 2.5,
					defaults: {
						columnWidth: 0.25,
						height: 30,
						margin: '3 3 3 3'
					},
					items: me.colItems
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
			var container = form.down('container'), 
			items = me.getItemsFromFormula(me.colItems, oldData);
			container.initItems = items;
			container.add(items);
		}
		return form;
	},
	/**
	 * 解析表达式
	 */
	getItemsFromFormula: function(source, oldData) {
		var sign = /[\+\-\*=\/%,\(\)\s]/, units = oldData.replace('[','(').replace(']',')')._split(sign), items = [],
			fns = ['abs', 'ceil', 'floor', 'round', 'nvl', 'nvl2', 'lpad', 'rpad', 'trim', 'trunc', 'to_char'], 
			cw = ['case', 'when', 'then', 'else', 'end', 'sysdate', '||'];
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
				Ext.Array.each(source,function(s){
					if(s.text ==unit){
						items.push(s);
					}
				});
			}
		});
		return items;
	},
	onUserDefinedClick: function(callback) {
		var me = this;
		var win = Ext.create('Ext.window.Window', {
			title : '自定义内容',
			closeAction: 'destroy',
			width : 300,
			items : [{
				xtype : 'textfield',
				emptyText : '输入除单引号外任意字符',
				width: '100%'
			}],
			buttons : [{
				text : $I18N.common.button.erpConfirmButton,
				height : 26,
				handler : function(b) {
					var w = b.ownerCt.ownerCt, f = w.down('textfield'), v = f.getValue();
					v && (callback.call(me, v));
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
	formula: function() {
		var me = this;
		me.formula_operator = [];
		return {
			log: function(oper, text, data, isfn) {
				me.formula_operator.push({oper: oper, text: text, data: data, isfn: isfn});
			},
			getContainer: function(scope) {
				return scope.up('form').down('container');
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
	testSQL:function(sql){
		var bool = false;
		Ext.Ajax.request({
			url : basePath + 'fs/credit/testSQL.action',
			async: false,
			params: {
				sql: sql
			},
			method : 'post',
			callback : function(options,success,response){
				var localJson = new Ext.decode(response.responseText);
				if(localJson.exceptionInfo){
					showError(localJson.exceptionInfo);
					return;
				}
				if(localJson.success){
					bool = true;
				}
			}
		});
		return bool;
	},
	splitdesc:function(sql,os) {
		var bool = true;
		var sign = /[\+\-\*=\/%,\(\)\s]/, units = os.replace('[','(').replace(']',')')._split(sign), items = [],
			fns = ['ABS', 'CEIL', 'FLOOR', 'ROUND', 'NVL', 'NVL2', 'LPAD', 'RPAD', 'TRIM', 'TRUNC', 'TO_CHAR']; 
			for(var i=0;i<units.length;i++){
				if(units[i]=='/'&&units[i+1]!='('){
					if(isNumber(units[i+1])){
						if(units[i+1]==0){
							showError('公式错误,0不能成为除数！');
							bool = false;
							break;
						}else{
							continue;
						}
					}else if(fns.indexOf(units[i+1]) > -1){
						var k1=0,k2=0,str=units[i+1];
						for(var j=i+2;j<units.length;j++){
							str+=units[j];
							if(units[j]=='('){
								k1++;
							}
							if(units[j]==')'){
								k2++;
							}
							if(k1==k2){
								break;
							}
						}
						sql+=' and '+str+'<>0';
						this.splitdesc(sql,str);
					}else{
						sql+=' and '+units[i+1]+'<>0';
					}
				}else if(units[i]=='/'&&units[i+1]=='('){
					var k1=1,k2=0,str=units[i+1];
					for(var j=i+2;j<units.length;j++){
						str+=units[j];
						if(units[j]=='('){
							k1++;
						}
						if(units[j]==')'){
							k2++;
						}
						if(k1==k2){
							break;
						}
					}
					sql+=' and '+str+'<>0';
					this.splitdesc(sql,str);
				}
			}
		return bool==true?sql:bool;
	},
	addScore:function(oldData){
		var me = this;
		var win = Ext.create('Ext.window.Window', {
			title : '评分公式',
			closeAction: 'destroy',
			width : 480,
			height:'75%',
			items : [me.getFormulaGrid(oldData)],
			buttonAlign : 'center',
			buttons : [{
				text : $I18N.common.button.erpConfirmButton,
				height : 26,
				style:'margin-right:20px;',
				handler : function(b) {
					var w = b.ownerCt.ownerCt,grid;
					grid = w.down('grid');
					var items = grid.store.data.items;
					var datas = new Array();
					Ext.Array.each(items,function(item){
						if(item.data['min']!=''&&item.data['max']!=''){
							if(parseFloat(item.data['min'])>parseFloat(item.data['max'])){
								showError('公式错误，最大值小于最小值！');
								datas.length = 0;
								return false;
							}
						}
						if(item.data['score']!='0'){
							datas.push(item.data);
						}
					});
					if(datas.length>0){
						var text='',sql='SELECT (CASE';
						var unit = Ext.getCmp('ct_unit');
						if(unit&&unit.value!=''){
							text+='单位：'+unit.rawValue+'；';
						}
						Ext.Array.each(datas,function(data){
							var add = data.score<0;
							sql+=' WHEN';					
							if(data.min!=''&&data.max===''){
								text+=data.min+'以上'+(add?'扣'+Math.abs(data.score):'加'+data.score)+'分；';
								sql+= ' CCT_ITEMVALUE>'+data.min+' THEN CCT_SCORE'+(add?data.score:'+'+data.score);
							}else if(data.min===''&&data.max!=''){
								text+=data.max+'以下'+(add?'扣'+Math.abs(data.score):'加'+data.score)+'分；';
								sql+= ' CCT_ITEMVALUE<'+data.max+' THEN CCT_SCORE'+(add?data.score:'+'+data.score);
							}else if(data.min!=''&&data.max!=''){
								text+=data.min+'-'+data.max+(add?'扣'+Math.abs(data.score):'加'+data.score)+'分；';
								sql+= ' CCT_ITEMVALUE>='+data.min+' AND CCT_ITEMVALUE<'+data.max+' THEN CCT_SCORE'+(add?data.score:'+'+data.score);
							}
						});
						sql+=' ELSE CCT_SCORE END) as score FROM CUSTCREDITTARGETS WHERE CCT_ID=v_id';
						var assessdesc = Ext.getCmp('ct_assessdesc');
						var assesssql = Ext.getCmp('ct_assesssql');
						if(assessdesc){
							assessdesc.setValue(text);
						}
						if(assesssql){
							assesssql.setValue(sql);
						}
						w.close();
					}
				}
			}, {
				text : $I18N.common.button.erpCloseButton,
				height : 26,
				style:'margin-left:20px;',
				handler : function(b) {
					var w = b.ownerCt.ownerCt;
					w.close();
				}
			}]
		});
		win.show();
	},
	getFormulaGrid:function(oldData){
		var sp = oldData.split(/[;；,，]/g);
		var data = new Array();
		Ext.Array.each(sp,function(s){
			var index = '';
			var obj = new Object();
			var score = '';
			var scorestring = '';
			var max = '';
			var min = '';
			if((index=s.indexOf('以上'))>0){
				min = s.substring(0,index);
				scorestring = s.substring(index+2);
				if(scorestring.indexOf('扣')>-1||scorestring.indexOf('减')>-1){
					score = '-'+scorestring.substring(1,scorestring.length-1);
				}else{
					score = scorestring.substring(1,scorestring.length-1);
				}
				obj.min = min;
				obj.score = score;
				data.push(obj);
			}else if((index=s.indexOf('以下'))>0){
				max = s.substring(0,index);
				scorestring = s.substring(index+2);
				if(scorestring.indexOf('扣')>-1||scorestring.indexOf('减')>-1){
					score = '-'+scorestring.substring(1,scorestring.length-1);
				}else{
					score = scorestring.substring(1,scorestring.length-1);
				}
				obj.max = max;
				obj.score = score;
				data.push(obj);
			}else if((index=s.indexOf('-'))>0){
				var num = s.match(/\d+/g);
				min = num[0];
				max = num[1];
				scorestring = s.substring(index+2);
				if('扣'.indexOf(s)>-1||'减'.indexOf(s)>-1){
					score = '-'+num[2];
				}else{
					score = num[2];
				}
				obj.max = max;
				obj.min = min;
				obj.score = score;
				data.push(obj);
			}
		});
		
		if(data.length==0){
			for(var i=0;i<10;i++){
				data.push({});
			}
		}
		
		var grid = Ext.create('Ext.grid.Panel', {
			bodyStyle : 'background:#f1f2f5;',
			columnLines : true,
			autoScroll :true ,
			layout:'fit',
			autoWidth:true,
			height: '100%',
			plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
				clicksToEdit: 1})],
			columns : [{
				xtype: 'rownumberer', 
				width: 35, 
				align: 'center'
			},{
				text : '最小值',
				flex:1,
				dataIndex : 'min',
				editor:'numberfield'
			},{
				text : '最大值',
				dataIndex : 'max',
				flex:1,
				editor:'numberfield'
			},{
				text : '加分',
				flex:1,
				dataIndex : 'score',
				editor:'numberfield'
			}],
			store : Ext.create('Ext.data.Store', {
				fields: [
						{name: 'min', type: 'string'},
	        			{name: 'max', type: 'string'},
						{name: 'score',type: 'number'}            
     			],
				data:data
			}),
			listeners:{
				itemclick:function(selModel, record){
					var me = this;
					var grid = selModel.ownerCt;
					if(grid){
						var index = grid.store.indexOf(record);
						if(index == grid.store.indexOf(grid.store.last())){
							var data = new Array();
							for(var i=0;i<10;i++){
								data.push({});
							}
							grid.store.loadData(data,true);
						}
				    }
				}
			}
		});
		return grid;
	}
});