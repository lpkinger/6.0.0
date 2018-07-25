Ext.define('erp.view.common.datalistFilter.ConContainer', {
	extend : 'Ext.form.FieldContainer',
	alias: 'widget.concontainer',
	layout : 'column',
	padding  : '4 0 0 0',
	FieldStore : null,
	defaults : {
		hideLabel : true
	},
	fieldName:null,
	conData:null,
	originalxtype:null,
	value:null,
	relationConf : {
		'textfield' : [{
			value : 'vague',
			display : '包含'
		}, {
			value : 'novague',
			display : '不包含'
		}, {
			value : 'head',
			display : '开头是'
		}, {
			value : 'end',
			display : '结尾是'
		}, {
			value : 'direct',
			display : '等于'
		}, {
			value : 'nodirect',
			display : '不等于'
		}, {
			value : 'null',
			display : '空白'
		}],
		'datefield' : [ {
			value : '=',
			display : '等于'
		}, {
			value : '>=',
			display : '开始于'
		}, {
			value : '<=',
			display : '结束于'
		}, {
			value : '~',
			display : '介于'
		} ],
		'numberfield' : [ {
			value : '=',
			display : '等于'
		}, {
			value : '!=',
			display : '不等于'
		}, {
			value : '>',
			display : '大于'
		}, {
			value : '>=',
			display : '大于等于'
		}, {
			value : '<',
			display : '小于'
		}, {
			value : '<=',
			display : '小于等于'
		}, {
			value : '~',
			display : '介于'
		} ],
		'combo' : [ {
			value : '=',
			display : '等于'
		} ]
	},
	initComponent : function() {
		this._initItems();
		this.callParent(arguments);
	},
	_initItems : function() {
        var items=new Array(),fieldItem=this.fieldItem=this.initFieldItem(),relationItem=this.relationItem=this.initRelationItem();
        items.push(fieldItem);
        items.push(relationItem);
        if(this.conData && this.conData.value&&this.conData.type!='null') {
            this.valueItem=this.initValueItem(this.conData.value);
            items.push(this.valueItem);
        }
        items.push({
			xtype:'button',						  
		  	cls:'clearformbutton',
		  	tooltip:'删除该行',
		    handler:function(){
		  	    var c=this.ownerCt,p=c.ownerCt;
		  		p.remove(c);
		  		if(p.items.length == 0){
		  			Ext.getCmp('saveAsButton')?Ext.getCmp('saveAsButton').setDisabled(true):'';
		  		}
		  	}
		 });
        Ext.apply(this,{
        	items:items 	
        });
	},
	//** 字段Combo *//*
	initFieldItem : function() {
		var store=this.FieldStore,v=null;
	    if(this.conData)v=this.conData.column_value;
		return {
			xtype : 'combo',
			displayField : 'text',
			valueField : 'field',
			columnWidth : 0.3,
			style:{
				"margin-left":"15px",
				"margin-right":"5px"
			},
			fieldStyle: "background:#DEDEDE;color:#515151;",
			multiSelect : false,
			editable : false,
			store:store,
			value:v,
			defaultListConfig : {
				loadMask : false
			},
			listeners : {
				select : function(field, datas) {
					var p = field.ownerCt,items=p.items.items,isChanged=false;
					var newType=datas[0].data.originalxtype;
					var text = datas[0].data.text;
					p.originalxtype=newType;
					p.text = text;
					//isChanged=newType!=p.originalxtype;
					for(var i=1;i<items.length;i++){					 
					  if(i>1){
						  if(items[i].xtype!='button'){p.remove(items[i]);i=i-1}
					  }
					  else items[i].setValue(null);
					}			
				}
			}
		};
	},
	//** 关联Combo *//*
	initRelationItem : function() {
		var x = this.originalxtype,combodata=x?this.relationConf[x]:[],v=null;
		if(this.conData && this.conData.type) v= this.conData.type;
		var c = {
			xtype : 'combo',
			displayField : 'display',
			valueField : 'value',
			columnWidth : 0.2,
			editable : false,
			value:v,
			style:{
				"margin-right":"5px",
			},
			store:Ext.create('Ext.data.Store',{
				   fields: ['value', 'display'],
				   data:combodata,
				   autoLoad:true
			}),
			defaultListConfig : {
				loadMask : false
			},
			onTriggerClick:function(){					  							
				var p=this.ownerCt;
				this.getStore().loadData(p.relationConf[p.originalxtype]);
				if (!this.readOnly && !this.disabled) {
				if (this.isExpanded) {
					this.collapse();
				} else {
					this.expand();
				}										
			  }
		    },
			listeners:{
				/**最好不使用change 防止冲突*/
				beforeselect:function(field,record){
					var p=field.ownerCt,items=Ext.clone(p.items.items),oldType=field.value,newType=record.data.value;
					if(newType!=oldType){
						for(var i=1;i<items.length;i++){
							if(i>1 && items[i].xtype!='button'){ 
								p.remove(items[i]);
							}
						}
					if(newType!='null'){
						p.add(2,p.initValueItem(null,newType));
					} 
					}					
				}				
			}
		}
		return c;
	},
	//** 值输入 *//*
	initValueItem : function(v,type) {
        var me=this,items=new Array();
        switch (me.originalxtype) {
		case 'combo':
			var item={
			   xtype:'combo',
			   hideLabel:true,
			   displayField:'display',
			   valueField: 'value',
			   columnWidth: 0.5,
			   editable:false,
			   value:v,
			   store:Ext.create('Ext.data.Store',{
				   fields: ['value', 'display'],
				   data:[],
				   autoLoad:true
			   }),
			   onTriggerClick:function(){					  							
					if (!this.readOnly && !this.disabled) {
						if (this.isExpanded) {
							this.collapse();
						} else {
							this.expand();
						}										
					}
			   },
			   listeners:{
				   afterrender:function(field){
					   var combodata=new Array(),p=field.ownerCt; 
						   datas=parent.Ext.getCmp(p.items.items[0].value+'Filter').store.data.items;	
						   Ext.Array.each(datas,function(d){
							 combodata.push(d.data);
					   });
					   field.getStore().loadData(combodata);
					   field.select(field.value);
				   }
			   }
		    };	
			items.push(item);
			break;
		case 'datefield':
			var o={
				xtype:'datefield',	   
				format:'Y-m-d',	
				columnWidth:0.5
		    };
		    var p=Ext.clone(o);
			if((v && v.indexOf('~')>0) || (type && type=='~')){
			   var v1=v?v.split('~')[0]:null,v2=v?v.split('~')[1]:null;				
			   var firstItem=Ext.apply(o,{
				  value:v1,
				  columnWidth:0.25,				  
				  listeners:{
					 change:function(t,newValue){
					 	    var nextField=t.ownerCt.items.items[3];
					 	    if(nextField.value && nextField.value<newValue){
					 	      nextField.setValue(null);
					 	    }
							t.ownerCt.items.items[3].setMinValue(newValue);
						 },
					 
					 }
			   });
			   items.push(firstItem);
			   var secondItem=Ext.apply(p,{
						  value:v2,
						  columnWidth:0.25,
						  listeners:{
							 change:function(t,newValue){
									var firstField=t.ownerCt.items.items[2];
									if(newValue!=null&&firstField&&firstField.value>newValue){
										firstField.setValue(null);
									}
									t.ownerCt.items.items[2].setMaxValue(newValue);
								}
						}
			   });
			   items.push(secondItem);
			}else {
				o.value=v;
				items.push(o);
			}
			break;
		case 'numberfield':
			var o={
				xtype:'numberfield',
				hideTrigger:true,
				columnWidth:0.5
		    };
		    var p=Ext.clone(o);
			if((v && v.indexOf('~')>0) || (type && type=='~')){
			   var v1=v?v.split('~')[0]:null,v2=v?v.split('~')[1]:null;	
			   var firstItem=Ext.apply(o,{
				  value:v1,
				  columnWidth:0.25,				
				  listeners:{
					 change:function(t,newValue){
					 		t.ownerCt.items.items[3].setMinValue(newValue);
						 }
					 }
			   });
			   items.push(firstItem);
			   var secondItem=Ext.apply(p,{
				  value:v2,
			      columnWidth:0.25,
				  listeners:{
					 change:function(t,newValue){
						t.ownerCt.items.items[2].setMaxValue(newValue);
					 }
				  }
			   });
			   items.push(secondItem);
			}else {
				o.value=v;
				items.push(o);
			}
			break;			
		default:
			items.push({
				xtype:'textfield',
				columnWidth:0.5,
				value:v				
			});			
	       break;
		}
		return items;
	},
	formatConditon:function(){
		var me=this,condition='',items=me.items.items,field=items[0].value,type=items[1].value,v1=items[2];		
		switch (me.originalxtype) {
		case 'combo':
			if(v1.value=='-无-'){
				condition="nvl(to_char("+field+"),' ')=' '";
			}else if(v1.value=='-所有-'){
				condition="1=1";
			}else{
				condition= "instr("+field+",'"+v1.value+"')=1";
			}	
			break;
		case 'datefield':
			condition+="to_char(" + field + ",'yyyy-MM-dd')";
			if(type=='~'){
				condition+=" between '" + Ext.util.Format.date(v1.value, 'Y-m-d') + "' and '"+ Ext.util.Format.date(items[3].value, 'Y-m-d') +"'";
			}else condition+=type+"'"+ Ext.util.Format.date(v1.value, 'Y-m-d')+"'";
			break;
		case 'numberfield':
			if(type=='~'){
				condition = field+" between "+v1.value+" and  "+items[3].value;
			}else if(type=='!='){
				condition="("+field+" "+type+" "+v1.value +" or "+field+" is null)";
			}else condition=field+" "+type+" "+v1.value;
			break;
		default:
		   condition += "(";
		   if(type=='vague'){
			   condition+="instr("+field+",'"+v1.value+"')>0)";
 		   }else if(type=='direct'){
 			   condition+=field+"='"+v1.value+"')";
 		   }else if(type=='nodirect'){
 			   condition+= field+"!='"+v1.value+"' or "+field+" is null) ";
 		   }else if(type=='novague'){
 			  condition+="instr("+field+",'"+v1.value+"')=0 or "+field+" is null)";
 		   }else if(type=='开头是'||type=='head'){
 			  condition+="instr("+field+",'"+v1.value+"')=1)";
 		   }else if(type=='结尾是'||type=='end'){
 			  condition+="instr("+field+",'"+v1.value+"',-1,1)=LENGTH("+field+")-length('"+v1.value+"')+1 and LENGTH("+field+")>=length('"+v1.value+"'))";
 		   }else if(type=='null'){
 			  condition+=field+' is null)';
			break;
		}
	  }	
	  return condition;	
	}
});