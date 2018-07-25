Ext.define('erp.view.core.form.SQLPanel', {
	extend : 'Ext.form.Panel',
	alias : 'widget.erpSQLPanel',
	id : 'sqlPanel',
	layout : 'border',
	autoScroll : true,
	width : 488,
	height : 400,
	frame : true,
	baseCls : 'my-panel-no-border',
	_noc : 0,
	dispArr : null,
	hideArr : null,
	flag:false,
	index:0,
	formatDisplay:false,
	requires: ['erp.view.core.button.TestSQL'],
	initComponent : function() {
		flag=false;
		dispArr = new Array();
		hideArr = new Array();
		Ext.apply(this, {
			items : [{
				xtype : 'toolbar',
				region:'north',
				baseCls : 'my-panel-no-border',
				ui: 'footer',
				layout : 'column',
				height:27,
				items : [{
					xtype : 'textfield',
					id : 'testText',
					columnWidth : 0.7,
					fieldLabel : '输入值',
					maxLength : 100,
					style:'padding-top:2',
					focusCls : 'x-form-field-cir-focus',
					labelAlign : "right",
					blankText : $I18N.common.form.blankText
				}, {
					xtype : 'button',
					text : '测试',
					style:'margin-left:5px',
					listeners : {
						click : {
							fn : this.test,
							scope : this
						}
					}
				}]
			}, {
				    id : 'dispText',
				    anchor : '100% 64%',
					xtype: 'htmleditor',
					region:'center',
					enableColors: false,
					enableAlignments: false,
					columnWidth:1,
					enableFont: false,
					enableFontSize: false,
					enableFormat: false,
					enableLinks: false,
					enableLists: false,
					readOnly:true,
					enableSourceEdit: false,
					height:120,
					labelWidth:105
			}, {
				xtype : 'panel',
				region:'south',
				baseCls : 'my-panel-no-border',
				anchor : '100% 30%',
				defaults:{
					xtype:'toolbar',
					ui:'footer',
					baseCls : 'my-panel-no-border'
				},
				items : [{					
					defaults:{
						handler:this.inputStr,
						scope:this,
						xtype:'erpSQLButton',
						highLight:true
					},
					items : [{					
						text : '输入值'					
					}, {				
						text : '标准分'		
					}, {						
						text : '最高分'				
					}, {					
						text : '最低分'						
					},'->',{
						text:'语法',
						handler:this.showDemo
					}]
				   },{
					defaults:{
						handler:this.inputStr,
						xtype:'erpSQLButton',
						scope:this
					},
					items : [{
						text : '1'													
					}, {						
						text : '2'													
					}, {				
						text : '3'						
					}, {				
						text : '+'
					}, {				
						text : '('
					}, {				
						text : ')'
					}, {				
						text : '='
					}, {
						text : '空格'
					}]
				}, {
					defaults:{
						handler:this.inputStr,
						xtype:'erpSQLButton',
						scope:this
					},
					items : [{
						text : '4'	
					}, {
						text : '5'				
					}, {						
						text : '6'					
					}, {				
						text : '-'					
					}, {				
						text : '<'					
					}, {
					    text : '>'				
					}, {			
						text : '>=',					
					}, {					
						text : '<='				
					}]
				}, {				
					defaults:{
						handler:this.inputStr,
						xtype:'erpSQLButton',
						scope:this
					},
					items : [{				
						text : '7'
					}, {
						text : '8'				
					}, {			
						text : '9'				
					}, {				
						text : '*'					
					}, {					
						text : '开始',
						highLight:true
					}, {					
						text : '如果',
						highLight:true
					}, {					
						text : '则',	
						highLight:true
					}, {
						text : '结束',
						highLight:true
					}]
				}, {				
					defaults:{
						handler:this.inputStr,
						xtype:'erpSQLButton',
						scope:this
					},
					items : [{
						text : '0'	
					}, {				
						text : '.'						
					}, {				
						text : '/'					
					}, {				
						text : '条件与',
						highLight:true
					}, {
						text : '条件或',
						highLight:true
					}, {
						text : '否则',
						highLight:true
					}, {				
						text : '删除',
						handler:this.delStr
					}, {			
						text : '清空',
						handler:this.celStr
					}]
				}]
			}]
		});		
		this.callParent(arguments);
		this.editSpace=this.items.items[1];
	},
	mappingStore:{
		'input':'输入值',
		'case':'开始',
		'when':'如果',
		'then':'则',
		'end':'结束',
		'or':'条件或',
		'and':'条件与',
		'else':'否则',
		'&nbsp;':'空格'
	},
	test : function(btn) {
		var me=this,input =' '+Ext.getCmp('testText').getValue();
		var tmpArr = Ext.Array.clone(hideArr);
		Ext.Array.forEach(hideArr, function(str, index, array) {
			if (str.trim() == 'input') {
				tmpArr[index] = input;
			}
		});
		var sql='select '+tmpArr.join("")+' from dual';
		Ext.Ajax.request({
			url : basePath + 'hr/KpiRule/testSQL.action',
			params : {
				sql :sql,
				caller : caller
			},
			method : 'post',
			callback : function(options, success, response) {
				var res = new Ext.decode(response.responseText);
				if (res.success) {
					flag=true;
					alert('测试通过');
					me.successAfter();
				} else if (res.exceptionInfo) {
					showError(res.exceptionInfo+'测试不通过，请重新输入!');
				}
			}
		});

	},
	inputStr : function(btn) {
		var _t=btn.text;
		
		if(btn.highLight){
			_t=' <font style="color:#800080;font-weight:600;" >['+btn.text+']</font>';
		}
		if(btn.text=='空格') {
			_t='&nbsp;';
			hideArr.push('');
		}else hideArr.push(this.getValueStr(btn.text));
		dispArr.push(_t);
		this.editSpace.setValue(this.editSpace.getValue()+_t);	
	},
	getValueStr:function(text){
		var me=this,valueF;
		for( var p in me.mappingStore){
			if(me.mappingStore[p]==text){
				valueF=' '+p+' ';
				break;
			}
		}
		return valueF||text;
	},
	delStr : function() {	
		var _disppoped=dispArr.pop(),_hidepoped=hideArr.pop(),_value=this.editSpace.getValue();
		this.editSpace.setValue(_value.substr(0,_value.lastIndexOf(_disppoped)));
	},
	celStr: function(){
		dispArr=new Array();
		hideArr=new Array();
		this.editSpace.setValue();
	},
	successAfter:function(){
		if(flag==true){
			Ext.getCmp('kr_sql').setValue(dispArr.join(""));
			Ext.getCmp('kr_sql1').setValue(hideArr.join(""));
			Ext.getCmp('kpisqlwin').close();
		}else{
			alert("测试通过才能保存");
		}
	},
	clearStr : function() {
		dispArr = [];
		hideArr = [];
		Ext.getCmp('dispText').setValue();
	},
	showDemo:function(btn,e){
		var win=Ext.getCmp('demo-sql'),me=this,_x=btn.getEl().getXY()[0];
		console.log(me.getHtml());
		if(!win){
			win=Ext.create('Ext.window.Window', {
	              id: 'demo-sql',
	              title: '语法样例',
	              height: window.innerHeight * 0.8,
	              width:250,
	              renderTo: Ext.getBody(),
	              animCollapse: false,
	              constrainHeader: true,
	              bodyBorder: false,
	              border: false,
	              x: screen.width-_x+142,
	              autoScroll:true,
	              frame:false,
	              html:me.getHtml(),
	              bodyStyle: {
	            	    background: '#f7f7f7'
	             }
			});
		}
	   win.show();
	},
	getHtml:function(){
		return this.getAndHtml()+this.getOrHtml()+this.getOrAndHtml()+this.getCaseHtml();
	
	},
	getAndHtml:function(){
		var me=this,title='<h4 class="sample-title">条件与语句</h4>';	
		return title+me.getHighLightHtmlByKey(['input','>60','and','input','<=80']);		      
	},
	getOrHtml:function(){
		var me=this,title='<h4 class="sample-title">条件或语句</h4>';	
		return title+me.getHighLightHtmlByKey(['input','>60','or','input','<=80']);		      
	},
	getOrAndHtml:function(){
		var me=this,title='<h4 class="sample-title">条件与或语句</h4>';	
		return title+me.getHighLightHtmlByKey(['(','input','>60','and','input','<=80',')','or','(','input','>80',')']);		      
	},
	getCaseHtml:function(){
		var me=this,title='<h4 class="sample-title">如果语句</h4>';	
		return title+me.getHighLightHtmlByKey(['case','when','input',' >80','then','A','when','input','>60','and','input','<=80','then','B','else','C','end']);		      
	},
	getHighLightHtmlByKey:function(keys){
		var _h="",me=this;
		Ext.each(keys,function(key){
			if(me.mappingStore[key]){
				if(key=='when' || key=='else' || key=='end')_h+='</br>&nbsp;&nbsp;';
				_h+='<font class="x-highLight" >['+me.mappingStore[key]+']</font> ';
				
			}else _h+=key;
			
		});
		return _h;
	}
});
