/**
 * 自动询价设置静态询价周期window
 * @author lidy
 * @since 2018-1-16
 */
Ext.define('erp.view.core.window.SelectInquiryDate', {
	extend: 'Ext.window.Window',
	alias: 'widget.selectinquirydatewindow',
	id:'selectinquirydatewindow',
	width: 850,
	height: 400,
	frame: true,
	closable : false,
	bodyStyle: 'background: #EDEDED;',
	layout: 'fit',
	title: '定时询价频率设置',
	count:1,
	date:[],
	month:'01',
	rowIndex:'',
	items: [{
		xtype: 'form',
		anchor: '100% 100%',
		bodyStyle: 'background: #EDEDED;',
		defaults: {
			margin: '20 10 30 10'
		},
		items: [{
			xtype: 'combo',
			fieldLabel: '周期选择',
			labelAlign: "left",
			id: 'inquiryrate',
			name: 'inquiryrate',
			editable : false,
			store: Ext.create('Ext.data.Store', {
			    fields: ['display', 'value'],
			    data : [
			        {"display":"年", "value":1},
			        {"display":"半年", "value":2},
			        {"display":"季度", "value":4},
			        {"display":"自定义", "value":0}
			    ]
			}),
			queryMode: 'local',
    		displayField: 'display',
    		valueField: 'value',
    		listeners:{
    			change:function(m,newValue,oldValue,e){
    				inquiryWin.changeInquiryrate(newValue);
    			}
    		}
		},{
			xtype:'panel',
			width: '100%',
			layout: 'hbox',
			items: [
				{
					xtype : 'form',
					region : 'center',
					value:'',
					title:'<font style="color:black">月</font>',
					height:'200',
			        flex:1,
			        id:'monthSelect',
			        items: [{
			        	xtype : 'radiogroup',
						vertical: true,
						defaultType: 'radiofield',
						id: 'monthRadio',
						layout:'column',
						defaults: {
			                columnWidth: 0.25,
			                listeners:{
								change:function(m,newvalue,oldvalue,e){
									if(newvalue){
										inquiryWin.selectMonth(m.inputValue);
									}
								}
							}
			            },
					items : []
			        }]
				},{
					xtype : 'form',
					region : 'center',
					value:'',
					title:'<font style="color:black">日</font>',
					height:'100%',
			        flex:1.2,
			        id:'daySelect',
			        items: [{
			        	xtype : 'radiogroup',
						vertical: true,
						defaultType: 'radiofield',
						id: 'dayRadio',
						layout:'column',
						defaults: {
			                columnWidth: 1/7,
			                listeners:{
								change:function(m,newvalue,oldvalue,e){
									if(newvalue){
										inquiryWin.selectDay(m.inputValue);
									}
								}
							}
			            },
						items : []
			        }]
				},{
					xtype: 'form',
					value:'',
					title:'<font style="color:black">推送频率</font>',
					height:'100%',
					layout:'fit',
			        flex:0.8,
			        items:[{
						xtype: 'textareafield',
						id: 'inquirydate',
				        readOnly : true
			        }],
			        tools:[{
			        	xtype:'button',
			        	text : '清除数据',
			        	handler: function() {
					        inquiryWin.cleanDate();
					    }
			        }]
				}
			]
		}],
		buttonAlign: 'center',
		buttons: [{
			text: '保存',
			cls: 'x-btn-blue',
			handler: function(btn) {
				inquiryWin.save(btn);
			}
		},{
			text: '关闭',
			cls: 'x-btn-blue',
			handler: function(btn) {
				var win = btn.up('window');
				win.close();
			}
		}]
	}],
	initComponent: function() {
		inquiryWin=this;
		this.title = '<div style="height:25;padding:5px 0 5px 0;color:black;font-size:14px;background: #f2f2f2;">&nbsp;&nbsp;' + this.title + '</div>';
		this.callParent(arguments);
		this.show();
		this.addMonth();
		this.addDay();
		this.setInquiryrate();
	},
	/**
	 * 设置频率下拉框
	 */
	setInquiryrate: function(){
		if(inquiryWin.count){
			Ext.getCmp('inquiryrate').setValue(parseInt(inquiryWin.count));
		}else{
			Ext.getCmp('inquiryrate').setValue(1);
		}
	},
	/**
	 * 添加月份
	 */
	addMonth: function(){
		var monthRadio = Ext.getCmp('monthRadio');
		monthRadio.add({
			boxLabel : '01月',
			name : 'month',
			inputValue : '01',
			id : 'month_1',
			checked: true
		});
		for(var i=2;i<10;i++){
			monthRadio.add({
				boxLabel : '0'+i+'月',
				name : 'month',
				inputValue : '0'+i,
				id : 'month_'+i
			})
		}
		for(var i=10;i<=12;i++){
			monthRadio.add({
				boxLabel : i+'月',
				name : 'month',
				inputValue : i.toString(),
				id : 'month_'+i
			})
		}
	},
	/**
	 * 添加日
	 */
	addDay: function(){
		var dayRadio = Ext.getCmp('dayRadio');
		for(var i=1;i<10;i++){
			dayRadio.add({
				boxLabel : '0'+i,
				name : 'day',
				inputValue : '0'+i,
				id : 'day_'+i
			})
		}
		for(var i=10;i<=31;i++){
			dayRadio.add({
				boxLabel : i,
				name : 'day',
				inputValue : i.toString(),
				id : 'day_'+i
			})
		}
	},
	/**
	 * 频率下拉框改变时
	 */
	changeInquiryrate: function(newValue){
		inquiryWin.count = newValue;
		var count = newValue;
		switch(count){
			case 1:
				inquiryWin.date=[];
		    	inquiryWin.date.push('12-30');
		    	break;
			case 2:
				inquiryWin.date=[];
		    	inquiryWin.date.push('06-30');
		    	inquiryWin.date.push('12-30');
		    	break;
		    case 4:
		    	inquiryWin.date=[];
		    	inquiryWin.date.push('03-30');
		    	inquiryWin.date.push('06-30');
		    	inquiryWin.date.push('09-30');
		    	inquiryWin.date.push('12-30');
		    	break;
		    default:
		    	inquiryWin.date=[];
		    	break;
		}
		
		var dayRadio = Ext.getCmp('dayRadio');
		var days = dayRadio.getChecked();
		Ext.each(days,function(me,index){
			me.setValue(false);
		})
		//设置推送频率显示的值
		inquiryWin.setInquiryDate();
	},
	/**
	 * 选择月份
	 */
	selectMonth: function(month){
		var month_31 = ['01','03','05','07','08','10','12'];
		var month_30 = ['04','06','09','11'];
		inquiryWin.month = month;
		if(Ext.Array.indexOf(month_31,month)!=-1){
			for(var i = 29;i<=31;i++){
				var monRadio = Ext.getCmp('day_'+i);
				monRadio.setVisible(true);
			}
		}else if(Ext.Array.indexOf(month_30,month)!=-1){
			var monRadio = Ext.getCmp('day_29');
			monRadio.setVisible(true);
			monRadio = Ext.getCmp('day_30');
			monRadio.setVisible(true);
			monRadio = Ext.getCmp('day_31');
			monRadio.setVisible(false);
		}else{
			for(var i = 29;i<=31;i++){
				var monRadio = Ext.getCmp('day_'+i);
				monRadio.setVisible(false);
			}
		}
		var dayRadio = Ext.getCmp('dayRadio');
		var days = dayRadio.getChecked();
		Ext.each(days,function(me,index){
			me.setValue(false);
		})
	},
	/**
	 * 选择日
	 */
	selectDay: function(day){
		var date = inquiryWin.date;
		var count = inquiryWin.count;
		var month = inquiryWin.month;
		var minTime = '1231' - '0101' ;
		var d = month + '-' + day ;
		var minDetno=0;
		var isBlank = 0;
		if(count==0){     //自定义频率
			minDetno = date.length;
			isBlank=1;
		}else{
			//获取最接近的时间
			for(var i=0 ; i<count ; i++){
				//判断是否为空
				if(date[i]==null||date[i]==''){
					minDetno = i;
					isBlank=1;
					break;
				}
				var time = Math.abs(date[i].replace('-','')-d.replace('-',''));
				if(time<minTime){
					minTime=time;
					minDetno = i;
				}
			}
		}
		inquiryWin.date[minDetno] = d;
		date = inquiryWin.date;
		var length = date.length;
		if(isBlank==1){
			//排序
			inquiryWin.date.sort();
		}
		//设置推送频率显示的值
		inquiryWin.setInquiryDate();
	},
	/**
	 * 清除数据
	 */
	cleanDate: function(){
		var count = inquiryWin.count;
		if(count==0){
			inquiryWin.date = [];
		}else{
			inquiryWin.date = new Array(count);
		}
		//设置推送频率显示的值
		inquiryWin.setInquiryDate();
	},
	/**
	 * 设置推送频率显示的值
	 */
	setInquiryDate: function(){
		var date = inquiryWin.date;
		var arr = new Array(date.length);
		for(var i = 0 ; i < date.length ; i++){
			if(date[i]!=null&&date[i]!=''){
				var d = date[i].split('-');
				arr[i] = Number(d[0])+'月' +Number(d[1])+ '日';
			}
		}
		var inquirydate = Ext.getCmp('inquirydate');
		if(inquirydate){
			inquirydate.setValue(arr.join('\n'));
		}
	},
	/**
	 * 保存
	 */
	save: function(btn){
		var count = inquiryWin.count;
		if(count!=0){
			for(var i = count-1 ; i >= 0 ; i--){
				if(inquiryWin.date[i]==null||inquiryWin.date[i]==''){
					showError('当前询价周期需要设置'+count+'个推送的日期');
					return;
				}
			}
		}
		var win = btn.up('window');
		win.close();
		if(inquiryWin.rowIndex===''){   //自动询价单主表
			var ai_jtinquirydate = Ext.getCmp('ai_jtinquirydate');  //静态询价设置
			if(ai_jtinquirydate){
				ai_jtinquirydate.setValue(inquiryWin.date.join(';'))
			}
			var ai_jtcycle = Ext.getCmp('ai_jtcycle');   //静态询价周期
			if(ai_jtcycle){
				ai_jtcycle.setValue(inquiryWin.count);
			}
			//保存到前端form中的字段
			var ai_jtnextdate = Ext.getCmp('ai_jtnextdate');  //静态下次询价日期
			if(ai_jtnextdate){
				ai_jtnextdate.setValue(inquiryWin.getNextdate());   
			}
		}else{   //自动询价单从表
			var grid = Ext.getCmp('grid');
			var store = grid.getStore();
			var rowStore = store.getAt(inquiryWin.rowIndex);
			if(rowStore && (caller=='ProdKindChange' || caller=='ProdChange')){
				rowStore.set('pcd_newjtinquirydate',inquiryWin.date.join(';'));  //静态询价设置
				rowStore.set('pcd_newjtcycle',inquiryWin.count);      //静态询价周期
				rowStore.set('pcd_newjtnextdate',inquiryWin.getNextdate());     //静态下次询价日期
			}else if(rowStore){
				rowStore.set('pk_jtinquirydate',inquiryWin.date.join(';'));  //静态询价设置
				rowStore.set('pk_jtcycle',inquiryWin.count);      //静态询价周期
				rowStore.set('pk_jtnextdate',inquiryWin.getNextdate());     //静态下次询价日期
			}
		}
	},
	/**
	 * 获取静态下次询价日期
	 */
	getNextdate: function() {
		for(var i=0 ; i<inquiryWin.date.length ; i++){     
			var dt1 = new Date('\''+inquiryWin.date[i]+'\'');
			var dt2 = Ext.Date.format(new Date(),'m-d');
			dt2 = new Date('\''+dt2+'\'');
			var dt3 = new Date('12-31');
			if(Ext.Date.between(dt1,dt2,dt3)){
				var date=new Date;  
				var year=date.getFullYear();
				return year + '-' + inquiryWin.date[i];
			}
		}
	}
});