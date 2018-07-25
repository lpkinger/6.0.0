Gef.ns('Gef.org');
Gef.org.CheckboxTreeNodeUI = Ext.extend(Ext.tree.CheckboxNodeUI, {
    checkParent: Ext.emptyFn,
    checkChild: Ext.emptyFn
});
Gef.org.OrgField = Ext.extend(Ext.form.TriggerField, {
	triggerClass : "x-form-search-trigger",
	
    initComponent: function() {
        this.readOnly = false;
        Gef.org.OrgField.superclass.initComponent.call(this);
        this.addEvents('select');
        
    },
    onTriggerClick: function() { 
    	var me = this;
    	
    	this.showWindow();
    },
  
    showWindow: function(store) {
    	var selete=[];
    	var seleteId=[];
     	var me = this;
    	var requestUrl  = '';
    	var jobFlag = false;
    	if(me.name=='assignee'||me.name=='notifyPeople'){
    		 var Morgname="or_name",Mname="em_name",Mcode="em_code";
    		 requestUrl = basePath+'common/getOrgAssignees.action';
    		 
    	}else{
    		var Morgname="JO_ORGNAME",Mname="jo_name",Mcode="jo_code";
    		 requestUrl = basePath+'common/getJobOfOrg.action';
    		 jobFlag = true;
    	}
    	
    	Ext.Msg.wait('获取数据中...');
    	Ext.Ajax.request({//拿到tree数据       	
    		url:requestUrl,        	
        	method:'post',
        	timeout:60000,
        	success: function(response){
        		Ext.Msg.hide();
        		res = new Ext.decode(response.responseText);
        		if(res.tree){
        		store = new Ext.decode(res.tree);
        		
        		var window=me.getWindow(store,selete,seleteId,Morgname,Mname,Mcode,jobFlag);
        		window.show();
                var value =window.field.value;
                var array = value.split(',');
              
                var array1=me.orgTree.getChecked();
            
                 for (var i = 0; i < array1.length; i++){                 	
                 	selete.push(array1[i]);
                 }
                

             
                window.items.items[0].form.items.items[3].setValue(me.orgTree.getChecked()); //设置  已选择    
        		} 
        	}
        });
        Gef.activeEditor.disable();
    },

    hideWindow: function() {
    	
        this.getWindow().hide();
        Gef.activeEditor.enable();
    },

    getWindow: function(store,selete,seleteId,Morgname,Mname,Mcode,jobFlag) {
        if (!this.orgWindow) {
            this.orgWindow = this.createWindow(store,selete,seleteId,Morgname,Mname,Mcode,jobFlag);
        }
        return this.orgWindow;
    },
   
    createWindow: function(store,selete,seleteId,Morgname,Mname,Mcode,jobFlag) {
    	var me = this;
    	
    	var selectedValues = [];    
    	if(this.value.indexOf(',')>0){
    		selectedValues = this.value.split(',');
    	}
    	
	
        var tree = new Ext.tree.TreePanel({
            autoScroll: true,
            loader: new Ext.tree.CustomUITreeLoader({               
                baseAttr: {
                    uiProvider: Gef.org.CheckboxTreeNodeUI
                }
            }),
            width:300,
            region:'center',
            enableDD: false,
            containerScroll: true,
            rootUIProvider: Gef.org.CheckboxTreeNodeUI,
            selModel: new Ext.tree.CheckNodeMultiSelectionModel(),
            rootVisible: false,
            count:1, // 这一行 是为了联合控制 click事件 加上去的,不属于树本身的属性:
            listeners:{
            	
            	'click':function(node){
            		
            		var window=me.getWindow(store);
            		
            		if(node.isLeaf()){
            			
            			if(node.ui.checkboxImg.className =='x-tree-node-checkbox-none'){
            			selete=arrayremove(selete,node.text);
            			
            			seleteId=arrayremove(seleteId,node.id);
            			window.items.items[0].form.items.items[3].setValue(selete);
            			
            		}else if((node.ui.checkboxImg.className =='x-tree-node-checkbox-all')){
            			
            			if(selete.indexOf(node.text)==-1){
            				
            				selete.push(node.text);
            				seleteId.push(node.id);
            			
            				window.items.items[0].form.items.items[3].setValue(selete);
            		}
            		}
            			
            		}else
            		{            			            			            			
            			if(node.ui.checkboxImg.className == 'x-tree-node-checkbox-all'){
            			
            				for(var i = 0; i < node.childNodes.length; i++) {
            				
            				node.ui.checkboxImg.className = 'x-tree-node-checkbox-all';
            				node.childNodes[i].ui.checkboxImg.className = 'x-tree-node-checkbox-all';
            				if(selete.indexOf(node.childNodes[i].text)==-1){
            					selete.push(node.childNodes[i].text);
            					
            					seleteId.push(node.childNodes[i].id);
            				}
            				
            				
            				window.items.items[0].form.items.items[3].setValue(selete);
	               			
               			 }
               		
               			
            			}
            			else if(node.ui.checkboxImg.className == 'x-tree-node-checkbox-none')
            			{
            				
            				for(var i = 0; i < node.childNodes.length; i++) {
            				
            				node.ui.checkboxImg.className = 'x-tree-node-checkbox-none';
            				node.childNodes[i].ui.checkboxImg.className = 'x-tree-node-checkbox-none';
            				selete=arrayremove(selete,node.childNodes[i].text);
            				seleteId=arrayremove(seleteId,node.childNodes[i].id);
            				window.items.items[0].form.items.items[3].setValue(selete);
            				
            				}
            				    
            			}
            		
            		}
           		
            	},
            	
            	'dbclick':function(){
            		
            		root = this.getRootNode();
            		var childrens = root.childNodes;
            		for(var i=0;i<childrens.length;i++){            			
            			for(var j=0;j<childrens[i].childNodes.length;j++){
            				if(childrens[i][j].attributes.qtip==me.value){
            					childrens[i][j].ui.checkboxImg.className = 'x-tree-node-checkbox-all';
            					childrens[i][j].attributes.checked = true;
            				}
            			}
            		}
            	},
            	'expandnode':function(node){
            		if(!node.isLeaf()){
            			var childrens = node.childNodes;
            			for(var i=0;i<childrens.length;i++){
            				if(childrens[i].isLeaf()){ // 传过来的 model.value(即之前保存过的值一定是  叶子节点额。)
            					if(selectedValues.length<2){ // model.value 只选择了一个值
            						if(childrens[i].attributes.qtip==me.value){
                						childrens[i].ui.checkboxImg.className = 'x-tree-node-checkbox-all';
                    					childrens[i].attributes.checked = true;
                    				}
            					}else{
            						for(var j=0;j<selectedValues.length;j++){ //model.value 选择了多个值!
            							if(childrens[i].attributes.qtip==selectedValues[j]){
                    						childrens[i].ui.checkboxImg.className = 'x-tree-node-checkbox-all';
                        					childrens[i].attributes.checked = true;
                        				}
            							
            						}
            						
            					}
            					
            				}
                		}
            		}
            			
            		
            	}
            	
            }
        });
        tree.getLoader().on('load', function(o, node) {
            if (node.isRoot) {
              tree.expandAll();
            }
        });
        var arrayindexof = function(val0,val1) {
        	
			for (var i = 0; i < val0.length; i++) {
						
				if (val0[i] == val1){ 					
					return i;					
				}	
			}
			return -1;
			};
       var  arrayremove = function(val0,val1) {       	       		
			var index =arrayindexof(val0,val1);
			
				if (index > -1) {
					val0.splice(index, 1);
					
					}
				return val0;	
				};
        
        var root = new Ext.tree.AsyncTreeNode({
            text: 'root',
            draggable: false,
            leaf:false,
            children:store,
            expanded:true
        });
        tree.setRootNode(root);
        
        tree.cleanCheck = function(node) {
        	if (typeof node == 'undefined') {
                node = this.rootVisible ? this.getRootNode() : this.getRootNode().firstChild;
            }
            if (node) {
                if (!node.isLeaf()) {
                	node.ui.checkboxImg.className ='x-tree-node-checkbox-none';
                    node.attributes.checked = false;
                    for(var i = 0; i < node.childNodes.length; i++) {
                        this.cleanCheck(node.childNodes[i]);
                    }
                }
            }

        };      
        tree.getChecked = function(node) {
            var checked = [], i;
            if (typeof node == 'undefined') {
                node = this.getRootNode();
            } else if (node.ui.checkboxImg && node.ui.checkboxImg.className == 'x-tree-node-checkbox-all' ) {            
            	if(node.isLeaf()){ 
            		checked.push(node.text);
            	} else {
            		node.ui.checkboxImg.className = 'x-tree-node-checkbox-none';
            	}           	 
            }
            if (!node.isLeaf()) {
            	for(var i = 0; i < node.childNodes.length; i++) {
                	checked = checked.concat(this.getChecked(node.childNodes[i]));
                }
            }
            return checked;
        };
        
        tree.setChecked = function(array) {
            for (var i = array.length - 1; i >= 0; i--) {            	
                var n = this.getNodeById(array[i]);               
                if (n && !n.getUI().checked()) {                	
                    n.getUI().check();
                }
            }
        };
        this.orgTree = tree; 
        //加筛选    根据 所属组织   人员名称  以及编号 做筛选
        var formpanel=new Ext.form.FormPanel({
        	 height:100,
        	 region:'west',
        	 width:'30%',
        	 labelWidth: 70,
             labelAlign: 'right',
             border: false,
             defaultType: 'textfield',
             defaults: {
                 anchor: '90%'
             },
             bodyStyle: {
                 padding: '6px 0 0'
             },
        	items:[{
        	  xtype:'textfield',
        	  name:'orname',
        	  fieldLabel:'所属组织'
        	},{
        	  xtype:'textfield', 
        	  name:'name',
        	  fieldLabel:jobFlag?'岗位名称':'员工名称'
        	},{
        	  xtype:'textfield',
        	  name:'code',
        	  fieldLabel:jobFlag?'岗位编号':'员工编号'
        	},{
        	  xtype: 'textarea',
        	  name:'selected',
        	  readOnly:true,
        	  //disabled:true,
        	  fieldLabel:'已选择'	
        	}],
        	buttonAlign:'center',
        	buttons:[{
        		text:'筛选',
        		iconCls:'x-form-search-trigger',
        		style:'padding-bottom:150px',
        		handler:function(){
        			   var condition=me.getCondition(win,Morgname,Mname,Mcode);
        			   
        		    	if(condition){
               			 var requestUrl  = '';
         		    	if(me.name=='assignee'||me.name=='notifyPeople'){
         		    		 requestUrl = basePath+'common/getOrgAssignees.action';
         		    	}else{
         		    		 requestUrl = basePath+'common/getJobOfOrg.action';
         		    	}
        			    Ext.Ajax.request({//拿到tree数据       	
        		    		url:requestUrl,
        		    		timeout:60000,
        		    		params:{
        		    			condition:condition
        		    		},
        		        	method:'post',
        		        	success: function(response){
        		        		res = new Ext.decode(response.responseText);
        		        		
        		        		if(res.tree){ 
        		        		if(joborgnorelation && Morgname=="JO_PARENTNAME"){
        		        			
        		        			//store = new Ext.decode(res.tree);
        		        		}else{
        		        		
        		        		var cstore = new Ext.decode(res.tree);
        		        		
        		        	    var hisroot=tree.getRootNode();
        		        	     
        		        	    var cn = hisroot.childNodes,n;
        		        	   
                                 
                                 while ((n = cn[0])) {
                            	    hisroot.removeChild(n);
                                  }
                                 var fn = function(node, ch) {
     		        				for(var i in ch) {    		  
     			        				var n = ch[i];
     			        				if(n.text){
     			        			    node.appendChild(new Ext.tree.AsyncTreeNode({
            		        	            text: n.text,
            		        	            draggable: false,
            		        	            leaf:false,
            		        	            children:n.children            		        	         
            		        	        }));  
     			        				}
     			        			}
     		        			};
     		        			fn(hisroot, cstore);
     		        			tree.expandAll();
                                 var value =win.field.value;
                                 var array = value.split(',');                                
                                  tree.setChecked(seleteId);
        		        		}
        		        		}	
 
        		        	}
        		        });
        		    	}
        		}
        	}]
        });
        var win = new Ext.Window({
            title: '人事',
            layout: 'border', 
            height:window.innerHeight*0.9,
            width: 600,
            closeAction: 'hide',
            modal: true,
            items: [formpanel,tree],
            buttons: [ {
                text: '确定',
                handler:function(){
                this.submit(selete);
               
                },
                scope: this
            }, {
                text: '取消',
                handler: this.hideWindow,
                scope: this
            }, {
                text: '刷新',
                handler: function() {
                	
                    tree.root.reload();
                    tree.setChecked(seleteId);
                    
                },
                scope: this
            }],
            listeners:{
            	'beforehide':function(c){
            		Gef.activeEditor.existWin=false;
            		Gef.activeEditor.enable();
            	},
            	'beforeshow':function(c){ 
            		Gef.activeEditor.existWin=true;
            		 Gef.activeEditor.disable();
            	}
            	
            }
        });
        win.field = this;
        return win;
    },
    submit: function(selete) {  
       
		var value='';
    	
    	  for (var i = 0; i < selete.length; i++) {
    	  	if(value==null||value==""){
    	  		value=value+selete[i];
    	  	}else{
    	  		value=value+','+selete[i];
    	  	}	
    	  }
 	  	this.setValue(value);    
        this.hideWindow();
        this.fireEvent('select', this);
    },

    selectOwner: function() {
        this.setValue("项目发起人");
        this.hideWindow();
        this.fireEvent('select', this);
    },

    refreshTree: function() {
    },
    getCondition:function(win,Morgname,Mname,Mcode){
    	var form=win.items.items[0].form;
    	var values=form.getValues();
    	var orname=values.orname;
    	var name=values.name;
    	var code=values.code;
    	var condition="";
    	if((!orname && !name && !code)|| (orname=="" && name=="" && code=="")){
    		return null;
    	}else {
    	   condition+=(orname==null || orname=="")?"1=1 #" :" "+Morgname+" like '%"+orname+"%' #";
    	   condition+=(name==null || name=="")?"1=1 " :" "+Mname+" like '%"+name+"%'";
    	   condition+=(code==null || code=="")?" " :" and "+Mcode+" like '%"+code+"%'";
    	   return condition;
    	}
    }
});

Ext.reg('orgfield', Gef.org.OrgField);

