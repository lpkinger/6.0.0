Ext.QuickTips.init();
Ext.define('erp.controller.hr.wage.conf.WageConfController',{
	extend:'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
	views:[
		'hr.wage.conf.Viewport',
		'hr.wage.conf.Form',
		'hr.wage.conf.PersonTaxGridPanel',
		'hr.wage.conf.AbsenceGridPanel',
		'hr.wage.conf.OverWorkGridPanel',
		'hr.wage.conf.FormulaTrigger',
		'core.trigger.DbfindTrigger',
		'core.button.Save',
		'core.button.Update',
		'core.button.Close'
		//grid
	],
	init:function(){
		var me = this;
		this.control({
        	'erpAbsenceGridPanel': {
                itemclick: this.onGridItemClick
            },
        	'erpOverWorkGridPanel': {
                itemclick: this.onGridItemClick
            },
        	'erpPersonTaxGridPanel': {
                itemclick: this.onGridItemClick
            },
            '#viewport':{
            	afterrender:function(){
					me.loadConfData();
            	}
            },
    		'erpUpdateButton': {
    			click: function(btn){
    				this.beforeUpdate();
    			}
    		},
    		'field[name=WC_ISFIXEDMONTHWORKDAYS]':{
    			select:function(t,record){
    				this.changeFormula(record[0].data.value);
    			}
    		}
		});
	},
	changeFormula:function(yn){
		
		var absencegrid = Ext.getCmp('absencegrid'),
		overworkgrid = Ext.getCmp('overworkgrid'),
		abstore = absencegrid.getStore(),
		owstore = overworkgrid.getStore();
		if (yn==0) {
			console.log(yn);
			//改为应勤天数
			//加班
			for (var i = 0; i < owstore.getCount(); i++) {
				var record = owstore.getAt(i),
				exptext = record.data.WO_EXPRESSIONTEXT
				exp = record.data.WO_EXPRESSION;
				record.set('WO_EXPRESSIONTEXT',exptext.replace("月平均工作天数","应勤天数"));
				record.set('WO_EXPRESSION',exp.replace("v_monthworkDays","v_shouldattendDays"));
			}
			//缺勤
			for (var i = 0; i < abstore.getCount(); i++) {
				var record = abstore.getAt(i),
				exptext = record.data.WAC_EXPRESSIONTEXT
				exp = record.data.WAC_EXPRESSION;
				record.set('WAC_EXPRESSIONTEXT',exptext.replace("月平均工作天数","应勤天数"));
				record.set('WAC_EXPRESSION',exp.replace("v_monthworkDays","v_shouldattendDays"));
			}			
			
			
			
			
			
		}else if(yn==1){
			console.log(yn);
			//改为月平均工作天数
			//加班
			for (var i = 0; i < owstore.getCount(); i++) {
				var record = owstore.getAt(i),
				exptext = record.data.WO_EXPRESSIONTEXT
				exp = record.data.WO_EXPRESSION;
				record.set('WO_EXPRESSIONTEXT',exptext.replace("应勤天数","月平均工作天数"));
				record.set('WO_EXPRESSION',exp.replace("v_shouldattendDays","v_monthworkDays"));
			}
			//缺勤
			for (var i = 0; i < abstore.getCount(); i++) {
				var record = abstore.getAt(i),
				exptext = record.data.WAC_EXPRESSIONTEXT
				exp = record.data.WAC_EXPRESSION;
				record.set('WAC_EXPRESSIONTEXT',exptext.replace("应勤天数","月平均工作天数"));
				record.set('WAC_EXPRESSION',exp.replace("v_shouldattendDays","v_monthworkDays"));
			}				
			
		}
	},
	loadConfData:function(){
		Ext.Ajax.request({
			url:basePath + 'hr/wage/conf/getAllConf.action',
			method : 'get',
			async:false,
			callback:function(records, options, response){
				var rs = Ext.decode(response.responseText);
				if (rs.success) {
					if (rs.baseconf) {
						var form = Ext.getCmp('form');
						var fields = form.getForm().getFields().items;
						Ext.Array.each(fields, function(field) {
							field.setValue(rs.baseconf[field.name])
						});
					}
					
					var absencegrid = Ext.getCmp('absencegrid');
					if (rs.abconf.length!=0) {
						absencegrid.getStore().loadData(rs.abconf);
					}else{
						absencegrid.getStore().loadData([{
							WAC_ID:null,WAC_TYPE:null,WAC_CONDEXPRESSION:null,WAC_CONDEXPRESSIONTEXT:null,WAC_EXPRESSION:null,WAC_EXPRESSIONTEXT:null
						}])
					}
					
					var overworkgrid = Ext.getCmp('overworkgrid');
					if (rs.owconf.length!=0) {
						overworkgrid.getStore().loadData(rs.owconf);
					}else{
						overworkgrid.getStore().loadData([{
							WO_ID:null,	WO_TYPE:null,WO_EXPRESSION:null,WO_EXPRESSIONTEXT:null
						}]);
					}
					
					var persontaxgrid = Ext.getCmp('persontaxgrid');
					if (rs.ptconf.length!=0) {
						persontaxgrid.getStore().loadData(rs.ptconf);
					}else{
						persontaxgrid.getStore().loadData([{
							WP_ID:null,WP_STARTAMOUNT:null,WP_ENDAMOUNT:null,WP_TAXRATE:null,WP_QUICKDEDUCTION:null
						}]);
					}
				}
			}
	 	});		
	},
	beforeUpdate:function(){
		var form = Ext.getCmp('form');
		if (form.getForm().isValid()) {
			
			var isfixedmonthworkdays = Ext.getCmp('isfixedmonthworkdays').value,
			    owStore = Ext.getCmp('overworkgrid').getStore(),
			    abStore = Ext.getCmp('absencegrid').getStore(),
		        str='';

			//检查不固定工作天数时，公式是否正确
			if (isfixedmonthworkdays==0) {
 				
			 	owIndex = owStore.findBy(function(record, i) {
			 		  str=record.get('WO_EXPRESSION');
			 		  if (str.indexOf('v_monthworkDays')>= 0) {
			 		  	   return true;
			 		  } 
				});
				
				if (owIndex!=-1) {
     					Ext.MessageBox.alert('提示','请将加班公式中的"月平均工作天数"修改为"应勤天数"');
     					return;					
				}
				
			 	abIndex = abStore.findBy(function(record, i) {
			 		  str=record.get('WAC_EXPRESSION');
			 		  if (str.indexOf('v_monthworkDays')>= 0) {
			 		  	   return true;
			 		  } 
				});
				
				if (abIndex!=-1) {
     					Ext.MessageBox.alert('提示','请将缺勤公式中的"月平均工作天数"修改为"应勤天数"');
     					return;					
				}				
				
			}else{
				
			 	owIndex = owStore.findBy(function(record, i) {
			 		  str=record.get('WO_EXPRESSION');
			 		  if (str.indexOf('v_shouldattendDays')>= 0) {
			 		  	   return true;
			 		  } 
				});
				
				if (owIndex!=-1) {
     					Ext.MessageBox.alert('提示','请将加班公式中的"应勤天数"修改为"月平均工作天数"');
     					return;					
				}
				
			 	abIndex = abStore.findBy(function(record, i) {
			 		  str=record.get('WAC_EXPRESSION');
			 		  if (str.indexOf('v_shouldattendDays')>= 0) {
			 		  	   return true;
			 		  } 
				});
				
				if (abIndex!=-1) {
     					Ext.MessageBox.alert('提示','请将缺勤公式中的"应勤天数"修改为"月平均工作天数"');
     					return;					
				}			
			
			}
			
			//对加班类型明细的限制
			
			
			//缺勤类型的限制
			
			
			var r = form.getValues();
     		this.onUpdate(r)
		}
	},
	onUpdate:function(r){
		var me = this;
		var params =  new Object();
		params.formStore = unescape(escape(Ext.JSON.encode(r)));
		
		var persontaxgrid = Ext.getCmp('persontaxgrid');
		params.ptgridStore = me.getGridStore(persontaxgrid)
		
		var overworkgrid = Ext.getCmp('overworkgrid');
		params.owgridStore = me.getGridStore(overworkgrid)
		
		var absencegrid = Ext.getCmp('absencegrid');
		params.abgridStore = me.getGridStore(absencegrid)
		
		Ext.Ajax.request({
			url:basePath + 'hr/wage/conf/update.action',
			params:params,
			method : 'post',
			async:false,
			callback:function(records, options, response){
				var rs = Ext.decode(response.responseText);
				if (rs.success) {
					window.location.reload();
				}else if (rs.exceptionInfo) {
					showError(rs.exceptionInfo);
				}
			}
	 	});
	},
	getGridStore: function(grid){
		if(grid == null){
			grid = Ext.getCmp('grid');
		}
		var me = this,
			jsonGridData = new Array();
		var form = Ext.getCmp('form');
		if(grid!=null){
			var s = grid.getStore().data.items;//获取store里面的数据
			for(var i=0;i<s.length;i++){//将grid里面各行的数据获取并拼成jsonGridData
				var data = s[i].data;
				dd = new Object();
				if((s[i].dirty)){
					Ext.each(grid.columns, function(c){
						if((!c.isCheckerHd)&&(c.logic != 'ignore') && c.dataIndex){//只需显示，无需后台操作的字段，自动略去
							if(c.xtype == 'datecolumn'){
								c.format = c.format || 'Y-m-d';
								if(Ext.isDate(data[c.dataIndex])){
									dd[c.dataIndex] = Ext.Date.format(data[c.dataIndex], c.format);
								} else {
									if(c.editor&&c.logic!='unauto'){
										dd[c.dataIndex] = Ext.Date.format(new Date(), c.format);//如果用户没输入日期，或输入有误，就给个默认日期，
									}else  dd[c.dataIndex]=null;
								}
							} else if(c.xtype == 'datetimecolumn'){
								if(Ext.isDate(data[c.dataIndex])){
									dd[c.dataIndex] = Ext.Date.format(data[c.dataIndex], 'Y-m-d H:i:s');//在这里把GMT日期转化成Y-m-d H:i:s格式日期
								} else {
									if(c.editor&&c.logic!='unauto'){
										dd[c.dataIndex] = Ext.Date.format(new Date(), 'Y-m-d H:i:s');//默认日期，
									}
								}
							} else if(c.xtype == 'numbercolumn'){//赋个默认值0吧，不然不好保存
								if(data[c.dataIndex] == null || data[c.dataIndex] == '' || String(data[c.dataIndex]) == 'NaN'){
									dd[c.dataIndex] = '0';//也可以从data里面去掉这些字段
								} else {
									dd[c.dataIndex] = "" + s[i].data[c.dataIndex];
								}
							} else {
								dd[c.dataIndex] = s[i].data[c.dataIndex];
							}
							if (c.defaultValue && (dd[c.dataIndex] == null || dd[c.dataIndex] == '0')) {
								dd[c.dataIndex] = c.defaultValue;
							}
						}
					});
					jsonGridData.push(Ext.JSON.encode(dd));
				}
			}
		}
		return jsonGridData;
	},
    onGridItemClick: function(selModel, record) { //grid行选择	
        this.GridUtil.onGridItemClick(selModel, record);
    }
});