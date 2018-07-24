Ext.define('erp.view.common.baseConfig.baseConfigForm',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.baseConfigForm',
	id: 'form', 
	frame : true,
	layout : 'hbox',
	padding:0,
	autoScroll : true,
	initComponent : function(){ 
		var me = this;
		var items = new Array();
		//获取系统维护管理数据
		var sys = me.addSysSetting();
		//按顺序排列节点信息（流程管理只取流程设计下的节点）
		items = me.findchildnode(sys.children,items);
		//获取高度和宽度
		var win = parent.Ext.getCmp('baseConfigWin');
		var height = win.el.dom.lastChild.clientHeight;
		var width = win.el.dom.lastChild.clientWidth;
		//设置每列panel最大子项
		var maxSize = parseInt(height/21);
		//panel集合
		var panels = new Array();
		Ext.each(items, function(btn, index){
			if(index!=0&&(index+1)%maxSize==0){
				var panel = {
					flex:1,
					xtype:'panel',
					layout:'vbox',
					items:items.slice(index+1-maxSize,index+1)	
				}
				panels.push(panel);
			}
			if((index+1)%maxSize!=0&&index+1==items.length){
				var n = (index+1)%maxSize;//最后一页余数
				var panel = {
					flex:1,
					xtype:'panel',
					layout:'vbox',
					items:items.slice(index+1-n,index+1)	
				}
				panels.push(panel);
			}
		});
		//大屏幕重置宽度		
		if(width>1000){
			var x = win.x;
			var y = win.y;
			win.setWidth(220*panels.length);
			win.showAt(x+(width-win.width)/2,y);
		}		
		Ext.apply(this,{
			items:panels	
		});
		this.callParent(arguments);
	},
	/**系统维护管理*/
	addSysSetting:function(){
		var json={};
		Ext.Ajax.request({
		    url: basePath+'resource/uucloud/syssetting.json',
		    async:false,
		    success: function(response){
		        var text = response.responseText;
		        json=new Ext.decode(text);
		    }
		});
		return json;		
	},
	findchildnode:function(childnodes,items){
		var win = parent.Ext.getCmp('baseConfigWin');
	    for(var i=0;i<childnodes.length;i++){
	    	var btn = {
	    		xtype:'displayfield',
	    		value: childnodes[i].text
	    	};	    	
	    	items.push(btn);
	    	if(btn.value=='流程管理'){
	    		for(var j=0;j<childnodes[i].children.length;j++){
	    			for(var x=0;x<childnodes[i].children[j].children.length;x++){
	    				var obj = childnodes[i].children[j].children[x];
			    		var text = obj.text,url = obj.url;
			    		if(!url&&text){
			    			var f=text.indexOf("f='")+3;
			    			var l=text.indexOf("' t");
			    			url = text.substring(f,l);
			    		}
			    		if(this.contains(text,'<a',true)){
			    			var f=text.indexOf("'>")+2;
			    			var l=text.indexOf("</a>");
			    			text = text.substring(f,l);		    			
			    		}		    	
			    		var btn = {
			    			xtype:'button',
			    			flex:1,
			    			tourl:url,
			    			text:text,
			    			style:'border:none;',
			    			listeners:{
			    				click:function(b){
			    					openUrl2(b.tourl,b.text);		    					
			    					win.close();
			    				}
			    			}
			    		};  	    
				    	items.push(btn);
	    			}
		    	}
	    	}else{
	    		for(var j=0;j<childnodes[i].children.length;j++){
		    		var obj = childnodes[i].children[j];
		    		var text = obj.text,url = obj.url,baseText=false;//a标签的跳转链接打开新窗口
		    		if(!url&&text){
		    			var f=text.indexOf("f='")+3;
		    			var l=text.indexOf("' t");
		    			url = text.substring(f,l);
		    		}
		    		if(this.contains(text,'<a',true)){
		    			var f=text.indexOf("'>")+2;
		    			var l=text.indexOf("</a>");
		    			text = text.substring(f,l);	
		    			baseText = true;
		    		}
		    		var btn = {
		    			xtype:'button',
		    			flex:1,
		    			tourl:url,
		    			baseText:baseText,
		    			text:text,
		    			style:'border:none;',
		    			listeners:{
		    				click:function(b){
		    					if(b.baseText){
		    						window.open(basePath + b.tourl,b.text)
		    					}else{
		    						openUrl2(b.tourl,b.text);	
		    					}
		    					win.close();
		    				}
		    			}
		    		};  	    
			    	items.push(btn);
		    	}
	    	}        
	    }
	    return items;
	},
	contains: function(string, substr, isIgnoreCase){
		if (string == null || substr == null) return false;
		if (isIgnoreCase === undefined || isIgnoreCase === true) {
			string = string.toLowerCase();
			substr = substr.toLowerCase();
		}
		return string.indexOf(substr) > -1;
	}
});