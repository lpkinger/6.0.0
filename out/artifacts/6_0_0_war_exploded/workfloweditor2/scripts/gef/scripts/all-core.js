
// eval
Geom = {};
Geom.Point = function(_, $) {
	this.x = _;
	this.y = $
};
Geom.Line = function(B, $, A, _) {
	this.x1 = B;
	this.y1 = $;
	this.x2 = A;
	this.y2 = _
};
Geom.Line.prototype.getX1 = function() {
	return this.x1
};
Geom.Line.prototype.getX2 = function() {
	return this.x2
};
Geom.Line.prototype.getY1 = function() {
	return this.y1
};
Geom.Line.prototype.getY2 = function() {
	return this.y2
};
Geom.Line.prototype.getK = function() {
	return (this.y2 - this.y1) / (this.x2 - this.x1)
};
Geom.Line.prototype.getD = function() {
	return this.y1 - this.getK() * this.x1
};
Geom.Line.prototype.getDistance = function() {
	return Math.sqrt((this.x1 - this.x2) * (this.x1 - this.x2)
			+ (this.y1 - this.y2) * (this.y1 - this.y2))
};
Geom.Line.prototype.isParallel = function($) {
	var A = this.x1, _ = this.x2;
	if ((Math.abs(A - _) < 0.01) && (Math.abs($.getX1() - $.getX2()) < 0.01))
		return true;
	else if ((Math.abs(A - _) < 0.01)
			&& (Math.abs($.getX1() - $.getX2()) > 0.01))
		return false;
	else if ((Math.abs(A - _) > 0.01)
			&& (Math.abs($.getX1() - $.getX2()) < 0.01))
		return false;
	else
		return Math.abs(this.getK() - $.getK()) < 0.01
};
Geom.Line.prototype.isSameLine = function(_) {
	if (this.isParallel(_)) {
		var A = _.getK(), $ = _.getD();
		if (Math.abs(this.x1 * A + $ - this.y1) < 0.01)
			return true;
		else
			return false
	} else
		return false
};
Geom.Line.prototype.contains = function(B) {
	var H = this.x1, C = this.y1, E = this.x2, D = this.y2, G = B.x, F = B.y, A = (H - E)
			* (H - E) + (C - D) * (C - D), _ = (G - H) * (G - H) + (F - C)
			* (F - C), $ = (G - E) * (G - E) + (F - D) * (F - D);
	return A > _ && A > $
};
Geom.Line.prototype.getCrossPoint = function(B) {
	if (this.isParallel(B))
		return null;
	var F, D;
	if (Math.abs(this.x1 - this.x2) < 0.01) {
		F = this.x1;
		D = B.getK() * F + B.getD()
	} else if (Math.abs(B.getX1() - B.getX2()) < 0.01) {
		F = B.getX1();
		D = this.getD()
	} else {
		var C = this.getK(), E = B.getK(), $ = this.getD(), _ = B.getD();
		F = (_ - $) / (C - E);
		D = C * F + $
	}
	var A = new Geom.Point(F, D);
	if (B.contains(A) && this.contains(A))
		return A;
	else
		return null
};
Geom.Line.prototype.getPerpendicularDistance = function(L, K) {
	var J = new Geom.Point(L, K);
	if (this.x1 == this.x2)
		return this.contains(J) ? Math.abs(this.x1 - L) : 999;
	if (this.y1 == this.y2)
		return this.contains(J) ? Math.abs(this.y1 - K) : 999;
	var _ = this.getK(), A = -1 / _, E = K - A * L, I = new Geom.Line(L, K, 0,
			E), B = this.getK(), D = I.getK(), F = this.getD(), G = I.getD(), C = (G - F)
			/ (B - D), $ = B * L + F, H = new Geom.Point(C, $);
	if (this.contains(H))
		return new Geom.Line(L, K, C, $).getDistance();
	else
		return 999
};
Geom.Rect = function(B, A, $, _) {
	this.x = B;
	this.y = A;
	this.w = $;
	this.h = _
};
Geom.Rect.prototype.getCrossPoint = function(A) {
	var $ = null, D = new Geom.Line(this.x, this.y, parseInt(this.x + this.w), this.y);
	$ = D.getCrossPoint(A);
	if ($ != null)
		return $;
	var _ = new Geom.Line(this.x, parseInt(this.y + this.h), parseInt(this.x + this.w), parseInt(this.y
					+ this.h));
	$ = _.getCrossPoint(A);
	if ($ != null)
		return $;
	var B = new Geom.Line(this.x, this.y, this.x, parseInt(this.y + this.h));
	$ = B.getCrossPoint(A);
	if ($ != null)
		return $;
	var C = new Geom.Line(parseInt(this.x + this.w), this.y, parseInt(this.x + this.w), parseInt(this.y
					+ this.h));
	$ = C.getCrossPoint(A);
	return $
};
function createCore(_) {
	var $ = {
		svgns : "http://www.w3.org/2000/svg",
		linkns : "http://www.w3.org/1999/xlink",
		vmlns : "urn:schemas-microsoft-com:vml",
		officens : "urn:schemas-microsoft-com:office:office",
		emptyFn : function() {
		},
		emptyArray : [],
		emptyMap : {},
		devMode : true,
		installVml : function() {
			if ($.isVml) {
				document.attachEvent("onreadystatechange", function() {
							var _ = document;
							if (_.readyState == "complete") {
								if (!_.namespaces["v"])
									_.namespaces.add("v", $.vmlns);
								if (!_.namespaces["o"])
									_.namespaces.add("o", $.officens)
							}
						});
				var _ = document.createStyleSheet();
				_.cssText = "v\\:*{behavior:url(#default#VML)}"
						+ "o\\:*{behavior:url(#default#VML)}"
			}
		},
		seed : 0,
		id : function() {
			if (!_)
				return "_INNER_" + this.seed++;
			else
				return "_" + _ + "_" + this.seed++
		},
		onReady : function($) {
			window.onload = function() {
				$()
			}
		},
		error : function(A, B) {
			if ($.devMode !== true)
				return;
			if ($.isVml) {
				var C = (B ? B : "") + "\n";
				for (var _ in A)
					C += _ + ":" + A[_] + "\n";
				$.debug(C)
			}
		},
		debug : function() {
			if (!$.debugDiv) {
				var A = document.createElement("div");
				A.style.position = "absolute";
				A.style.left = "50px";
				A.style.top = "50px";
				document.body.appendChild(A);
				var B = document.createElement("textarea");
				B.rows = 10;
				B.rols = 40;
				A.appendChild(B);
				var C = document.createElement("button");
				C.innerHTML = "close";
				C.onclick = function() {
					A.style.display = "none"
				};
				A.appendChild(C);
				$.debugDiv = A;
				$.debugTextArea = B
			}
			var _ = "";
			for (var D = 0; D < arguments.length; D++)
				_ += "," + arguments[D];
			$.debugTextArea.value += "\n" + _;
			$.debugDiv.style.display = ""
		},
		getInt : function($) {
			$ += "";
			$ = $.replace(/px/, "");
			var _ = parseInt($, 10);
			return isNaN(_) ? 0 : _
		},
		extend : function() {
			var A = function($) {
				for (var _ in $)
					this[_] = $[_]
			}, _ = Object.prototype.constructor;
			return function(F, E, D) {
				if (typeof E == "object") {
					D = E;
					E = F;
					F = D.constructor != _ ? D.constructor : function() {
						E.apply(this, arguments)
					}
				}
				var B = function() {
				}, C, G = E.prototype;
				B.prototype = G;
				C = F.prototype = new B();
				C.constructor = F;
				F.superclass = G;
				if (G.constructor == _)
					G.constructor = E;
				C.override = A;
				$.override(F, D);
				return F
			}
		}(),
		override : function(C, _) {
			if (_) {
				var A = C.prototype;
				for (var B in _)
					A[B] = _[B];
				if ($.isIE && _.toString != C.toString)
					A.toString = _.toString
			}
		},
		ns : function() {
			for (var E = 0; E < arguments.length; E++) {
				var _ = arguments[E], A = _.split("."), C = window[A[0]] = window[A[0]]
						|| {}, $ = A.slice(1);
				for (var D = 0; D < $.length; D++) {
					var B = $[D];
					C = C[B] = C[B] || {}
				}
			}
			return C
		},
		apply : function(C, A, _) {
			if (_)
				$.apply(C, _);
			if (C && A && typeof A == "object")
				for (var B in A)
					C[B] = A[B];
			return C
		},
		applyIf : function(A, $) {
			if (A && $)
				for (var _ in $)
					if (typeof A[_] == "undefined")
						A[_] = $[_];
			return A
		},
		join : function(_) {
			var $ = "";
			for (var A = 0; A < _.length; A++)
				$ += _[A];
			return $
		},
		getTextSize : function(A) {
			if (!$.textDiv) {
				$.textDiv = document.createElement("div");
				$.textDiv.style.position = "absolute";
				$.textDiv.style.fontFamily = "Verdana";
				$.textDiv.style.fontSize = "12px";
				$.textDiv.style.left = "-1000px";
				$.textDiv.style.top = "-1000px";
				document.body.appendChild($.textDiv)
			}
			var B = $.textDiv;
			B.innerHTML = A;
			var _ = {
				w : Math.max(B.offsetWidth, B.clientWidth),
				h : Math.max(B.offsetHeight, B.clientHeight)
			};
			return _
		},
		notBlank : function($) {
			if (typeof $ == "undefined")
				return false;
			else if (typeof $ == "string" && $.trim().length == 0)
				return false;
			return true
		},
		safe : function($) {
			if ($)
				return $.trim();
			else
				return ""
		},
		get : function($) {
			return document.getElementById($)
		},
		value : function(_, B) {
			var A = $.get(_);
			if (typeof B != "undefined")
				A.value = $.safe(B);
			return $.safe(A.value)
		},
		each : function(C, A, $) {
			if (typeof C.length == "undefined" || typeof C == "string")
				C = [C];
			for (var B = 0, _ = C.length; B < _; B++)
				if (A.call($ || C[B], C[B], B, C) === false)
					return B
		},
		showMessage : function(_, $) {
			alert($)
		},
		isEmpty : function($) {
			if (typeof $ == "undefined")
				return true;
			if ($ == null)
				return true;
			if (typeof $.length != "undefined" && $.length == 0)
				return true;
			return false
		},
		notEmpty : function($) {
			return !this.isEmpty($)
		}
	};
	(function() {
		var F = navigator.userAgent.toLowerCase(), E = F.indexOf("opera") > -1, B = (/webkit|khtml/)
				.test(F), H = !E && F.indexOf("msie") > -1, _ = !E
				&& F.indexOf("msie 7") > -1, A = !E && F.indexOf("msie 8") > -1, D = !B
				&& F.indexOf("gecko") > -1, C = H || _ || A, G = !C;
		$.isSafari = B;
		$.isIE = H;
		$.isIE7 = _;
		$.isGecko = D;
		$.isVml = C;
		$.isSvg = G;
		if (C)
			$.installVml();
		$.applyIf(Array.prototype, {
					indexOf : function($) {
						for (var A = 0, _ = this.length; A < _; A++)
							if (this[A] === $)
								return A;
						return -1
					},
					remove : function(_) {
						var $ = this.indexOf(_);
						if ($ != -1)
							this.splice($, 1);
						return this
					}
				});
		String.prototype.trim = function() {
			var $ = /^\s+|\s+$/g;
			return function() {
				return this.replace($, "")
			}
		}()
	})();
	return $
}
Gef = createCore("Gef");
Gef.IMAGE_ROOT = "gef/images/activities/48/";
Gef.ns("Gef.ui");
Gef.ui.WorkbenchWindow = Gef.extend(Ext.Window, {
			getActivePage : Gef.emptyFn
		});
Gef.ns("Gef.ui");
Gef.ui.WorkbenchPage = Gef.extend(Object, {
			getWorkbenchWindow : Gef.emptyFn,
			getActiveEditor : Gef.emptyFn,
			openEditor : Gef.emptyFn
		});
Gef.ns("Gef.ui");
Gef.ui.WorkbenchPart = Gef.extend(Object, {
			setWorkbenchPage : Gef.emptyFn,
			getWorkbenchPage : Gef.emptyFn
		});
Gef.ns("Gef.ui");
Gef.ui.ViewPart = Gef.extend(Object, {});
Gef.ns("Gef.ui");
Gef.ui.EditorPart = Gef.extend(Gef.ui.WorkbenchPart, {
			init : Gef.emptyFn
		});
Gef.ns("Gef.ui");
Gef.ui.EditorInput = Gef.extend(Object, {
			getName : Gef.emptyFn,
			getObject : Gef.emptyFn
		});
Gef.ns("Gef.ui.support");
Gef.ui.support.DefaultWorkbenchWindow = Gef.extend(Gef.ui.WorkbenchWindow, {
	getActivePage : function() {
		if (!this.activePage) {
			this.activePage = new Gef.ui.support.DefaultWorkbenchPage();
			this.activePage.setWorkbenchWindow(this)
		}
		return this.activePage
	},
	render : function() {
		if (!this.rendered) {
			document.getElementsByTagName("html")[0].className += " gef-workbenchwindow";
			if (Gef.isIE) {
				this.width = document.body.offsetWidth;
				this.height = document.body.offsetHeight
			} else {
				this.width = window.innerWidth;
				this.height = window.innerHeight
			}
			this.getActivePage().render();
			this.rendered = true
		}
	},
	getActivePage2:function(a){
		if (!this.activePage) {
			this.activePage = new Gef.ui.support.DefaultWorkbenchPage();
			this.activePage.setWorkbenchWindow(a)
		}
		return this.activePage
	}
	
});
Gef.ns("Gef.ui.support");
Gef.ui.support.DefaultWorkbenchPage = Gef.extend(Gef.ui.WorkbenchPage, {
			constructor : function($) {
				this.workbenchWindow = $
			},
			getWorkbenchWindow : function() {
				return this.workbenchWindow
			},
			setWorkbenchWindow : function($) {
				this.workbenchWindow = $
			},
			getActiveEditor : function() {
				return this.activeEditor
			},
			openEditor : function(_, $) {
				this.activeEditor = _;
				_.setWorkbenchPage(this);
				_.init($)
			},
			render : function() {
				this.activeEditor.render()
			}
		});
Gef.ns("Gef.ui.support");
Gef.ui.support.DefaultEditorPart = Gef.extend(Gef.ui.EditorPart, {
			constructor : function($) {
				this.workbenchPage = $
			},
			getWorkbenchPage : function() {
				return this.workbenchPage
			},
			setWorkbenchPage : function($) {
				this.workbenchPage = $
			},
			init : function($) {
			},
			render : function() {
			}
		});
Gef.ns("Gef.commands");
Gef.commands.Command = Gef.extend(Object, {
			execute : Gef.emptyFn,
			undo : Gef.emptyFn,
			redo : Gef.emptyFn
		});
Gef.ns("Gef.commands");
Gef.commands.CommandStack = Gef.extend(Object, {
			constructor : function() {
				this.undoList = [];
				this.redoList = [];
				this.maxUndoLength = 100
			},
			execute : function($) {
				while (this.undoList.length > this.maxUndoLength)
					this.undoList.shift();
				this.undoList.push($);
				this.redoList.splice(0, this.redoList.length);
				$.execute();
				return $
			},
			redo : function() {
				var $ = this.redoList.pop();
				if ($ != null) {
					this.undoList.push($);
					$.redo();
					return this.redoList.length > 0
				}
				return false
			},
			undo : function() {
				var $ = this.undoList.pop();
				if ($ != null) {
					while (this.redoList.length > this.maxUndoLength)
						this.redoList.shift();
					this.redoList.push($);
					$.undo();
					return this.undoList.length > 0
				}
				return false
			},
			flush : function() {
				this.flushUndo();
				this.flushRedo()
			},
			flushUndo : function() {
				this.undoList.splice(0, this.undoList.length)
			},
			flushRedo : function() {
				this.redoList.splice(0, this.redoList.length)
			},
			getMaxUndoLength : function() {
				return this.maxUndoLength
			},
			setMaxUndoLength : function($) {
				this.maxUndoLength = $
			},
			canUndo : function() {
				return this.undoList.length > 0
			},
			canRedo : function() {
				return this.redoList.length > 0
			}
		});
Gef.ns("Gef.commands");
Gef.commands.CompoundCommand = Gef.extend(Gef.commands.Command, {
			constructor : function() {
				this.commandList = []
			},
			addCommand : function($) {
				this.commandList.push($)
			},
			getCommandList : function() {
				return this.commandList
			},
			execute : function() {
				for (var $ = 0; $ < this.commandList.length; $++)
					this.commandList[$].execute()
			},
			undo : function() {
				for (var $ = this.commandList.length - 1; $ >= 0; $--)
					this.commandList[$].undo()
			},
			redo : function() {
				for (var $ = 0; $ < this.commandList.length; $++)
					this.commandList[$].redo()
			}
		});
Gef.ns("Gef.figure");
//修改边框颜色  //2012-08-24 1765 1783
Gef.figure.Figure = Gef.extend(Object, {
			constructor : function($) {
				this.children = [];
				$ = $ ? $ : {};
				$["fill"] = $["fill"] || "";
				$["strok"] = $["strok"] || "black";
				$["strokwidth"] = $["strokwidth"] || 1;
				Gef.apply(this, $)
			},
			setParent : function($) {
				this.parent = $
			},
			getParent : function() {
				return this.parent
			},
			getParentEl : function() {
				return this.parent.el
			},
			getChildren : function() {
				return this.children
			},
			addChild : function($) {
				this.children.push($);
				$.setParent(this)
			},
			removeChild : function($) {
				$.remove()
			},
			render : function() {
				if (!this.el)
					if (Gef.isVml) {
						this.renderVml();
						this.onRenderVml()
					} else {
						this.renderSvg();
						this.onRenderSvg()
					}
				for (var $ = 0; $ < this.children.length; $++)
					this.children[$].render()
			},
			renderSvg : Gef.emptyFn,
			renderVml : Gef.emptyFn,
			onRenderVml : function() {
				this.el.setAttribute("id", Gef.id());
				this.el.style.position = "absolute";
				this.el.style.cursor = "pointer";//"pointer"
				this.el.fillcolor = this["fill"];
				this.el.strokecolor = this["stroke"];
				this.el.strokeweight = this["strokewidth"];
				this.getParentEl().appendChild(this.el)
			},
			onRenderSvg : function() {
				this.el.setAttribute("id", Gef.id());
				this.el.setAttribute("fill", this["fill"]);
				this.el.setAttribute("stroke", this["stroke"]);
				this.el.setAttribute("stroke-width", this["strokewidth"]);
				this.el.setAttribute("cursor", "pointer"); //rendersvg 样式
				this.getParentEl().appendChild(this.el)
			},
			getId : function() {
				return this.el.getAttribute("id")
			},
			remove : function() {
				this.parent.getChildren().remove(this);
				this.getParentEl().removeChild(this.el)
			},
			show : function() {
				this.el.style.display = ""
			},
			hide : function() {
				this.el.style.display = "none"
			},
			moveTo : Gef.emptyFn,
			update : Gef.emptyFn
		});
Gef.ns("Gef.figure");
Gef.figure.GroupFigure = Gef.extend(Gef.figure.Figure, {
			renderVml : function() {
				var $ = document.createElement("div");
				$.id = this.id;
				this.el = $;
				this.getParentEl().appendChild($)
			},
			renderSvg : function() {
				var $ = document.createElementNS(Gef.svgns, "g");
				$.setAttribute("id", this.id);
				this.el = $;
				this.getParentEl().appendChild($)
			},
			onRenderVml : function() {
			},
			onRenderSvg : function() {
			}
		});
Gef.ns("Gef.figure");
Gef.figure.LineFigure = Gef.extend(Gef.figure.Figure, {
			renderVml : function() {
				var $ = document.createElement("v:line");
				$.from = this.x1 + "," + this.y1;
				$.to = this.x2 + "," + this.y2;
				this.el = $
			},
			renderSvg : function() {
				var $ = document.createElementNS(Gef.svgns, "line");
				$.setAttribute("x1", this.x1 + "px");
				$.setAttribute("y1", this.y1 + "px");
				$.setAttribute("x2", this.x2 + "px");
				$.setAttribute("y2", this.y2 + "px");
				this.el = $
			},
			onRenderVml : function() {
				this.el.setAttribute("id", Jpdl.id());
				this.el.style.position = "absolute";
				this.el.style.cursor = "pointer";
				this.el.setAttribute("strokeweight", 2);
				this.el.setAttribute("strokecolor", "blue");
				this.getParentEl().appendChild(this.el)
			},
			onRenderSvg : function() {
				this.el.setAttribute("id", Jpdl.id());
				this.el.setAttribute("fill", "white");
				this.el.setAttribute("stroke", "blue");
				this.el.setAttribute("stroke-width", "2");
				this.el.setAttribute("cursor", "pointer");
				this.getParentEl().appendChild(this.el)
			},
			update : function(B, $, A, _) {
				this.x1 = B;
				this.y1 = $;
				this.x2 = A;
				this.y2 = _;
				if (Gef.isVml)
					this.updateVml();
				else
					this.updateSvg()
			},
			updateVml : function() {
				this.el.from = this.x1 + "," + this.y1;
				this.el.to = this.x2 + "," + this.y2
			},
			updateSvg : function() {
				this.el.setAttribute("x1", this.x1 + "px");
				this.el.setAttribute("y1", this.y1 + "px");
				this.el.setAttribute("x2", this.x2 + "px");
				this.el.setAttribute("y2", this.y2 + "px")
			}
		});
Gef.ns("Gef.figure");
Gef.figure.PolylineFigure = Gef.extend(Gef.figure.Figure, {
			getTools : function() {
				return []
			},
			getPoint : function(_, A) {
				var $ = "";
				for (var C = 0; C < this.points.length; C++) {
					var B = this.points[C];
					$ += (B[0] + _) + "," + (B[1] + A) + " "
				}
				return $
			},
			renderVml : function() {
				var $ = document.createElement("v:polyline");
				$.setAttribute("points", this.getPoint(0, 0));
				this.el = $
			},
			renderSvg : function() {
				var $ = document.createElementNS(Gef.svgns, "polyline");
				$.setAttribute("points", this.getPoint(0, 0));
				this.el = $
			},
			onRenderVml : function() {
				this.el.setAttribute("id", Gef.id());
				this.el.style.position = "absolute";
				this.el.style.cursor = "pointer";
				this.el.setAttribute("strokeweight", 2);
				this.el.setAttribute("strokecolor", "blue");
				Gef.model.root.appendChild(this.el)
			},
			onRenderSvg : function() {
				this.el.setAttribute("id", Gef.id());
				this.el.setAttribute("fill", "none");
				this.el.setAttribute("stroke", "blue");
				this.el.setAttribute("stroke-width", "2");
				this.el.setAttribute("cursor", "pointer");
				Gef.model.root.appendChild(this.el)
			},
			onSelectVml : function() {
				this.el.setAttribute("strokeweight", "4");
				this.el.setAttribute("strokecolor", "green")
			},
			onSelectSvg : function() {
				this.el.setAttribute("stroke-width", "4");
				this.el.setAttribute("stroke", "green")
			},
			onDeselectVml : function() {
				this.el.setAttribute("strokeweight", "2");
				this.el.setAttribute("strokecolor", "blue")
			},
			onDeselectSvg : function() {
				this.el.setAttribute("stroke-width", "2");
				this.el.setAttribute("stroke", "blue")
			}
		});
Gef.ns("Gef.figure");
Gef.figure.RectFigure = Gef.extend(Gef.figure.Figure, {
			renderVml : function() {
				var $ = document.createElement("v:rect");
				$.style.left = this.x + "px";
				$.style.top = this.y + "px";
				$.style.width = this.w + "px";
				$.style.height = this.h + "px";
				this.el = $
			},
			renderSvg : function() {
				var $ = document.createElementNS(Gef.svgns, "rect");
				$.setAttribute("x", this.x + "px");
				$.setAttribute("y", this.y + "px");
				$.setAttribute("width", this.w + "px");
				$.setAttribute("height", this.h + "px");
				this.el = $
			},
			move : function($, _) {
				this.moveTo(this.x + $, this.y + _)
			},
			moveTo : function(_, $) {
				this.x = _;
				this.y = $;
				if (Gef.isVml)
					this.moveToVml();
				else
					this.moveToSvg()
			},
			moveToVml : function() {
				this.el.style.left = this.x + "px";
				this.el.style.top = this.y + "px"
			},
			moveToSvg : function(_, $) {
				this.el.setAttribute("x", this.x);
				this.el.setAttribute("y", this.y)
			},
			update : function(B, A, $, _) {
				this.x = B;
				this.y = A;
				this.w = $;
				this.h = _;
				if (Gef.isVml)
					this.updateVml();
				else
					this.updateSvg()
			},
			updateVml : function() {
				this.moveToVml();
				this.el.style.width = this.w + "px";
				this.el.style.height = this.h + "px"
			},
			updateSvg : function() {
				this.moveToSvg();
				this.el.setAttribute("width", this.w);
				this.el.setAttribute("height", this.h)
			},
			resize : function(B, _, A) {
				var E = this.x, D = this.y, $ = this.w, C = this.h;
				if (B == "n") {
					D = D + A;
					C = C - A
				} else if (B == "s")
					C = C + A;
				else if (B == "w") {
					E = E + _;
					$ = $ - _
				} else if (B == "e")
					$ = $ + _;
				else if (B == "nw") {
					E = E + _;
					$ = $ - _;
					D = D + A;
					C = C - A
				} else if (B == "ne") {
					$ = $ + _;
					D = D + A;
					C = C - A
				} else if (B == "sw") {
					E = E + _;
					$ = $ - _;
					C = C + A
				} else if (B == "se") {
					$ = $ + _;
					C = C + A
				}
				this.update(E, D, $, C);
				return {
					x : E,
					y : D,
					w : $,
					h : C
				}
			}
		});
Gef.ns("Gef.figure");
Gef.figure.RoundRectFigure = Gef.extend(Gef.figure.RectFigure, {
			renderVml : function() {
				Gef.figure.RoundRectFigure.superclass.renderVml.call(this);
				this.el.arcsize = 0.2
			},
			renderSvg : function() {
				Gef.figure.RoundRectFigure.superclass.renderSvg.call(this);
				this.el.setAttribute("rx", 10);
				this.el.setAttribute("ry", 10)
			},
			onRenderVml : function() {
				this.el.setAttribute("id", Gef.id());
				this.el.style.position = "absolute";
				this.el.style.cursor = "pointer";
				this.getParentEl().appendChild(this.el)
			},
			onRenderSvg : function() {
				this.el.setAttribute("id", Gef.id());
				this.el.setAttribute("cursor", "pointer");
				this.getParentEl().appendChild(this.el)
			}
		});
Gef.ns("Gef.figure");
Gef.figure.ImageFigure = Gef.extend(Gef.figure.RectFigure, {
			renderVml : function() {
				var $ = document.createElement("img");
				$.style.left = this.x + "px";
				$.style.top = this.y + "px";
				$.setAttribute("src", this.url);
				this.el = $
			},
			renderSvg : function() {
				var $ = document.createElementNS(Gef.svgns, "image");
				$.setAttribute("x", this.x + "px");
				$.setAttribute("y", this.y + "px");
				$.setAttribute("width", this.w + "px");
				$.setAttribute("height", this.h + "px");
				$.setAttributeNS(Gef.linkns, "xlink:href", this.url);
				$.onclick = function() {
					return false
				};
				this.el = $
			},
			update : function(B, A, $, _) {
				this.moveTo(B, A)
			},
			onRenderVml : function() {
				this.el.setAttribute("id", Gef.id());
				this.el.style.position = "absolute";
				this.el.style.cursor = "pointer";
				this.getParentEl().appendChild(this.el)
			},
			onRenderSvg : function() {
				this.el.setAttribute("id", Gef.id());
				this.el.setAttribute("cursor", "pointer");
				this.getParentEl().appendChild(this.el)
			},
			changeImageUrl : function($) {
				if (Gef.isVml)
					this.changeImageUrlVml($);
				else
					this.changeImageUrlSvg($)
			},
			changeImageUrlVml : function($) {
				this.el.setAttribute("src", $)
			},
			changeImageUrlSvg : function($) {
				this.el.setAttributeNS(Gef.linkns, "xlink:href", $)
			}
		});
Gef.ns("Gef.figure");
Gef.figure.RootFigure = Gef.extend(Gef.figure.Figure, {
			render : function() {
				this.getParentEl().onselectstart = function() {
					return false
				};
				Gef.figure.RootFigure.superclass.render.call(this)
			},
			renderVml : function() {
				var $ = document.createElement("div");
				$.setAttribute("id", Gef.id());
				this.getParentEl().appendChild($);
				this.el = $
			},
			renderSvg : function() {
				var E = this.getParentEl(), _ = E.ownerDocument
						.createElementNS(Gef.svgns, "svg");
				_.setAttribute("id", Gef.id());
				_.setAttribute("width", E.style.width.replace(/px/, ""));
				_.setAttribute("height", E.style.height.replace(/px/, ""));
				_.style.fontFamily = "Verdana";
				_.style.fontSize = "12px";
				E.appendChild(_);
				var $ = _.ownerDocument.createElementNS(Gef.svgns, "defs");
				_.appendChild($);
				var B = _.ownerDocument.createElementNS(Gef.svgns, "marker");
				B.setAttribute("id", "markerArrow");
				B.setAttribute("markerUnits", "userSpaceOnUse");
				B.setAttribute("markerWidth", 8);
				B.setAttribute("markerHeight", 8);
				B.setAttribute("refX", 8);
				B.setAttribute("refY", 4);
				B.setAttribute("orient", "auto");
				var A = _.ownerDocument.createElementNS(Gef.svgns, "path");
				A.setAttribute("d", "M 0 0 L 8 4 L 0 8 z");
				A.setAttribute("stroke", "#909090");
				A.setAttribute("fill", "#909090");
				B.appendChild(A);
				$.appendChild(B);
				var D = _.ownerDocument.createElementNS(Gef.svgns, "marker");
				D.setAttribute("id", "markerDiamond");
				D.setAttribute("markerUnits", "userSpaceOnUse");
				D.setAttribute("markerWidth", 16);
				D.setAttribute("markerHeight", 8);
				D.setAttribute("refX", 0);
				D.setAttribute("refY", 4);
				D.setAttribute("orient", "auto");
				var C = _.ownerDocument.createElementNS(Gef.svgns, "path");
				C.setAttribute("d", "M 0 4 L 8 8 L 16 4 L 8 0 z");
				C.setAttribute("stroke", "#909090");
				C.setAttribute("fill", "#FFFFFF");
				D.appendChild(C);
				$.appendChild(D);
				this.el = _
			},
			onRenderVml : function() {
			},
			onRenderSvg : function() {
			}
		});
Gef.ns("Gef.figure");
Gef.figure.NoFigure = Gef.extend(Gef.figure.Figure, {
			render : Gef.emptyFn,
			update : Gef.emptyFn
		});
Gef.ns("Gef.figure");
Gef.figure.NodeFigure = Gef.extend(Gef.figure.RoundRectFigure, {
	constructor : function($) {
		this.outputs = [];
		this.incomes = [];
		Gef.figure.NodeFigure.superclass.constructor.call(this, $);
		this.w = 90;
		this.h = 50
	},
	renderVml : function() {
		var $ = document.createElement("v:group");
		$.style.left = this.x;
		$.style.top = this.y;
		$.style.width = this.w;
		$.style.height = this.h;
		$.setAttribute("coordsize", this.w + "," + this.h);
		this.el = $;
		var B = document.createElement("v:roundrect");
		B.style.position = "absolute";
		B.style.left = "5px";
		B.style.top = "5px";
		B.style.width = (this.w - 10) + "px";
		B.style.height = (this.h - 10) + "px";
		B.setAttribute("id", Gef.id());
		B.setAttribute("arcsize", 0.2);
		B.setAttribute("fillcolor", "#F6F7FF");
		B.setAttribute("strokecolor", "#03689A");
		B.setAttribute("strokeweight", "2");
		B.style.verticalAlign = "middle";
		$.appendChild(B);
		this.rectEl = B;
		var _ = this.getTextPosition(this.w, this.h), A = document
				.createElement("v:textbox");
		A.style.textAlign = "center";
		A.style.fontFamily = "Verdana";
		A.style.fontSize = "12px";
		A.setAttribute("id", Gef.id());
		A.innerHTML = this.name;
		B.appendChild(A);
		this.textEl = A
	},
	renderSvg : function(ak47) {
		if(!ak47){
		var $ = document.createElementNS(Gef.svgns, "g");
		$.setAttribute("transform", "translate(" + this.x + "," + this.y + ")");
		this.el = $;
		var B = document.createElementNS(Gef.svgns, "rect");
		B.setAttribute("id", Gef.id());
		B.setAttribute("x", 5);
		B.setAttribute("y", 5);
		B.setAttribute("width", (this.w - 10) + "px");
		B.setAttribute("height", (this.h - 10) + "px");
		B.setAttribute("rx", 10);
		B.setAttribute("ry", 10);
		B.setAttribute("fill", "#F6F7FF");
		B.setAttribute("stroke", "#b4b4b4");//hey 基础颜色
		B.setAttribute("stroke-width", "2");
		$.appendChild(B);
		this.rectEl = B;
		var _ = this.getTextPosition(this.w, this.h), A = document
				.createElementNS(Gef.svgns, "text");
		A.setAttribute("id", Gef.id());
		A.setAttribute("x", _.x);
		A.setAttribute("y", _.y);
		A.setAttribute("text-anchor", "middle");
		A.textContent = this.name;
		$.appendChild(A);
		this.textEl = A
		}else{
			var $ = document.createElementNS(Gef.svgns, "g");
			$.setAttribute("transform", "translate(" + this.x + "," + this.y + ")");
			this.el = $;
			var B = document.createElementNS(Gef.svgns, "rect");
			B.setAttribute("id", Gef.id());
			B.setAttribute("x", 5);
			B.setAttribute("y", 5);
			B.setAttribute("width", (this.w - 10) + "px");
			B.setAttribute("height", (this.h - 10) + "px");
			B.setAttribute("rx", 10);
			B.setAttribute("ry", 10);
			B.setAttribute("fill", "red");
			B.setAttribute("stroke", "#03689A");
			B.setAttribute("stroke-width", "2");
			$.appendChild(B);
			this.rectEl = B;
			var _ = this.getTextPosition(this.w, this.h), A = document
					.createElementNS(Gef.svgns, "text");
			A.setAttribute("id", Gef.id());
			A.setAttribute("x", _.x);
			A.setAttribute("y", _.y);
			A.setAttribute("text-anchor", "middle");
			A.textContent = this.name;
			$.appendChild(A);
			this.textEl = A
		}
	},
	onRenderVml : function() {
		this.el.setAttribute("id", Gef.id());
		this.el.style.position = "absolute";
		this.el.style.cursor = "pointer";
		this.getParentEl().appendChild(this.el)
	},
	onRenderSvg : function() {
		this.el.setAttribute("id", Gef.id());
		this.el.setAttribute("cursor", "pointer");
		this.getParentEl().appendChild(this.el)
	},
	getTextPosition : function($, _) {
		if (Gef.isVml)
			return this.getTextPositionVml($, _);
		else
			return this.getTextPositionSvg($, _)
	},
	getTextPositionVml : function($, B) {
		var _ = Gef.getTextSize(this.name), C = $ / 2, A = B / 2;
		return {
			x : C + "px",
			y : A + "px"
		}
	},
	getTextPositionSvg : function($, B) {
		var _ = Gef.getTextSize(this.name), C = $ / 2, A = B / 2 + _.h / 4;
		return {
			x : C + "px",
			y : A + "px"
		}
	},
	moveTo : function(B, _) {
		Gef.figure.NodeFigure.superclass.moveTo.call(this, B, _);
		for (var A = 0; A < this.incomes.length; A++) {
			var $ = this.incomes[A];
			$.refresh()
		}
		for (A = 0; A < this.outputs.length; A++) {
			$ = this.outputs[A];
			$.refresh()
		}
	},
	moveToVml : function() {
		this.el.style.left = this.x + "px";
		this.el.style.top = this.y + "px"
	},
	moveToSvg : function($, _) {
		this.el.setAttribute("transform", "translate(" + this.x + "," + this.y
						+ ")")
	},
	update : function(B, A, $, _) {
		this.x = B;
		this.y = A;
		this.w = $;
		this.h = _;
		if (Gef.isVml)
			this.resizeVml(B, A, $, _);
		else
			this.resizeSvg(B, A, $, _)
	},
	remove : function() {
		for (var _ = this.outputs.length - 1; _ >= 0; _--) {
			var $ = this.outputs[_];
			$.remove()
		}
		for (_ = this.incomes.length - 1; _ >= 0; _--) {
			$ = this.incomes[_];
			$.remove()
		}
		Gef.figure.NodeFigure.superclass.remove.call(this)
	},
	hideText : function() {
		this.textEl.style.display = "none"
	},
	updateAndShowText : function($) {
		this.name = $;
		if (Gef.isVml)
			this.textEl.innerHTML = $;
		else
			this.textEl.textContent = $;
		this.textEl.style.display = ""
	},
	cancelEditText : function() {
		this.textEl.style.display = ""
	},
	resize : function(B, _, A) {
		var E = this.x, D = this.y, $ = this.w, C = this.h;
		if (B == "n") {
			D = D + A;
			C = C - A
		} else if (B == "s")
			C = C + A;
		else if (B == "w") {
			E = E + _;
			$ = $ - _
		} else if (B == "e")
			$ = $ + _;
		else if (B == "nw") {
			E = E + _;
			$ = $ - _;
			D = D + A;
			C = C - A
		} else if (B == "ne") {
			$ = $ + _;
			D = D + A;
			C = C - A
		} else if (B == "sw") {
			E = E + _;
			$ = $ - _;
			C = C + A
		} else if (B == "se") {
			$ = $ + _;
			C = C + A
		}
		if (Gef.isVml)
			this.resizeVml(E, D, $, C);
		else
			this.resizeSvg(E, D, $, C);
		return {
			x : E,
			y : D,
			w : $,
			h : C
		}
	},
	resizeVml : function(B, A, $, _) {
		this.el.style.left = B + "px";
		this.el.style.top = A + "px";
		this.el.style.width = $ + "px";
		this.el.style.height = _ + "px";
		this.el.coordsize = $ + "," + _;
		this.rectEl.style.width = ($ - 10) + "px";
		this.rectEl.style.height = (_ - 10) + "px"
	},
	resizeSvg : function(C, B, $, A) {
		this.el.setAttribute("transform", "translate(" + C + "," + B + ")");
		this.rectEl.setAttribute("width", ($ - 10) + "px");
		this.rectEl.setAttribute("height", (A - 10) + "px");
		var _ = this.getTextPosition($, A);
		this.textEl.setAttribute("x", _.x);
		this.textEl.setAttribute("y", _.y)
	},
	getTools : function() {
		return []
	}
});
Gef.ns("Gef.figure");
Gef.figure.ImageNodeFigure = Gef.extend(Gef.figure.ImageFigure, {
			constructor : function($) {
				this.w = 48;
				this.h = 48;
				this.outputs = [];
				this.incomes = [];
				Gef.figure.ImageNodeFigure.superclass.constructor.call(this, $)
			},
			move : function(_, A) {
				Gef.figure.ImageNodeFigure.superclass.move.call(this, _, A);
				for (var B = 0; B < this.incomes.length; B++) {
					var $ = this.incomes[B];
					$.refresh()
				}
				for (B = 0; B < this.outputs.length; B++) {
					$ = this.outputs[B];
					$.refresh()
				}
			},
			remove : function() {
				for (var _ = this.outputs.length - 1; _ >= 0; _--) {
					var $ = this.outputs[_];
					$.remove()
				}
				for (_ = this.incomes.length - 1; _ >= 0; _--) {
					$ = this.incomes[_];
					$.remove()
				}
				Gef.figure.ImageNodeFigure.superclass.remove.call(this)
			},
			getTools : function() {
				return []
			}
		});
Gef.ns("Gef.figure");
Gef.figure.EdgeFigure = Gef.extend(Gef.figure.PolylineFigure, {
	getTools : function() {
		if (!this.tools){
			this.pointTools = true;//标识 hey
			//连线使用工具按钮 hey
			this.tools = [new Gef.jbs.tool.SetLineTool()];
		}
		if(_ReadOnly){
			this.tools = []
		}
		return this.tools
	},
	constructor : function(_, $) {
		this.from = _;
		this.to = $;
		if (!this.name)
			this.name = "to " + $.name;
		/*if(this.name.indexOf("cancel")>0) this.name='不同意';*/
		if(this.name.indexOf("END")>0) this.name='完成'; //连线默认
		this.from.outputs.push(this);
		this.to.incomes.push(this);
		this.alive = true;
		this.innerPoints = [];
		this.calculate();
		Gef.figure.EdgeFigure.superclass.constructor.call(this, {});
		this.textX = 0;
		this.textY = 0;
		this.conditional = false
	},
	render : function() {
		this.calculate();
		Gef.figure.EdgeFigure.superclass.render.call(this);
		this.setConditional(this.conditional)
	},
	onRenderVml : function() {
		this.el.setAttribute("id", Gef.id());
		this.el.style.position = "absolute";
		this.el.style.cursor = "pointer";
		this.el.setAttribute("strokeweight", 2);
		this.el.setAttribute("strokecolor", "#909090");
		this.getParentEl().appendChild(this.el);
		this.stroke = document.createElement("v:stroke");
		this.el.appendChild(this.stroke);
		this.stroke.setAttribute("endArrow", "Classic");
		this.fill = document.createElement("v:fill");
		this.el.appendChild(this.fill);
		this.fill.setAttribute("opacity", 0);
		var _ = document.createElement("textbox");
		_.setAttribute("id", Gef.id());
		var $ = this.getTextLocation();
		_.style.position = "absolute";
		_.style.left = $.x + "px";
		_.style.top = ($.y - $.h) + "px";
		_.style.textAlign = "center";
		_.style.cursor = "pointer";
		_.style.fontFamily = "Verdana";
		_.style.fontSize = "12px";
		_.innerHTML = this.name ? this.name : "";
		_.setAttribute("edgeId", this.getId());
		this.getParentEl().appendChild(_);
		this.textEl = _
	},
	onRenderSvg : function() {
		this.el.setAttribute("id", Gef.id());
		this.el.setAttribute("fill", "none");
		this.el.setAttribute("stroke", "#909090");
		this.el.setAttribute("stroke-width", "2");
		this.el.setAttribute("cursor", "pointer");
		this.el.setAttribute("marker-end", "url(#markerArrow)");
		this.getParentEl().appendChild(this.el);
		var _ = document.createElementNS(Gef.svgns, "text");
		_.setAttribute("id", Gef.id());
		var $ = this.getTextLocation();
		_.setAttribute("x", $.x);
		_.setAttribute("y", $.y - 4);
		_.setAttribute("cursor", "pointer");
		_.textContent = this.name ? this.name : "";
		_.setAttribute("edgeId", this.getId());
		this.getParentEl().appendChild(_);
		this.textEl = _
	},
	setConditional : function($) {
		this.conditional = $;
		if (Gef.isVml)
			this.setConditionalVml();
		else
			this.setConditionalSvg()
	},
	setConditionalVml : function() {
		if (this.conditional === true)
			this.stroke.setAttribute("startArrow", "diamond");
		else
			this.stroke.setAttribute("startArrow", "none")
	},
	setConditionalSvg : function() {
		if (this.conditional === true)
			this.el.setAttribute("marker-start", "url(#markerDiamond)");
		else
			this.el.setAttribute("marker-start", "")
	},
	calculate : function() {
		var A = new Geom.Line(parseInt(this.from.x + this.from.w / 2),parseInt( this.from.y
						+ this.from.h / 2), parseInt(this.to.x + this.to.w / 2), parseInt(this.to.y
						+ this.to.h / 2)), C = new Geom.Rect(this.from.x,
				this.from.y, this.from.w, this.from.h), B = new Geom.Rect(
				this.to.x, this.to.y, this.to.w, this.to.h), _ = C
				.getCrossPoint(A), $ = B.getCrossPoint(A);
		if (_ == null || $ == null) {
			this.x1 = 0;
			this.y1 = 0;
			this.x2 = 0;
			this.y2 = 0
		} else {
			this.x1 = _.x;
			this.y1 = _.y;
			this.x2 = $.x;
			this.y2 = $.y
		}
		this.convert()
	},
	recalculate : function(_, $) {
		var B = new Geom.Line(parseInt(_.x + _.w / 2),parseInt( _.y + _.h / 2), $[0], $[1]), C = new Geom.Rect(
				_.x, _.y, _.w, _.h), A = C.getCrossPoint(B);
		return A
	},
	convert : function() {
		this.points = [];
		var _ = this.points, A = this.innerPoints.length;
		if (A > 0) {
			var $ = this.recalculate(this.from, this.innerPoints[0]);
			if ($) {
				this.x1 = $.x;
				this.y1 = $.y
			}
		}
		_.push([this.x1, this.y1]);
		for (var B = 0; B < this.innerPoints.length; B++)
			_.push([this.innerPoints[B][0], this.innerPoints[B][1]]);
		if (A > 0) {
			$ = this.recalculate(this.to, this.innerPoints[A - 1]);
			if ($) {
				this.x2 = $.x;
				this.y2 = $.y
			}
		}
		_.push([this.x2, this.y2])
	},
	update : function(B, $, A, _) {
		this.x1 = B;
		this.y1 = $;
		this.x2 = A;
		this.y2 = _;
		if (Gef.isVml)
			this.updateVml();
		else
			this.updateSvg()
	},
	updateVml : function() {
		this.el.points.value = this.getPoint(0, 0);
		var $ = this.getTextLocation();
		this.textEl.style.left = $.x + "px";
		this.textEl.style.top = ($.y - $.h) + "px"
	},
	updateSvg : function() {
		this.el.setAttribute("points", this.getPoint(0, 0));
		var $ = this.getTextLocation();
		this.textEl.setAttribute("x", $.x);
		this.textEl.setAttribute("y", $.y - 4)
	},
	refresh : function() {
		if (!this.el)
			this.render();
		this.calculate();
		this.update(this.x1, this.y1, this.x2, this.y2)
	},
	getTextLocation : function() {
		var _ = Gef.getTextSize(this.name), $ = _.w + 2, B = _.h + 2, C = parseInt((this.x1 + this.x2)
				/ 2 + this.textX - 1), A = parseInt((this.y1 + this.y2) / 2 + this.textY
				+ 2);
		return {
			x : C,
			y : A,
			w : $,
			h : B
		}
	},
	updateAndShowText : function(_) {
		this.name = _;
		if (Gef.isVml) {
			this.textEl.innerHTML = _ ? _ : "";
			var $ = this.getTextLocation();
			this.textEl.style.left = $.x;
			this.textEl.style.top = $.y
		} else
			this.textEl.textContent = _ ? _ : "";
		this.textEl.style.display = ""
	},
	remove : function() {
		if (this.alive) {
			this.from.outputs.remove(this);
			this.to.incomes.remove(this);
			this.getParentEl().removeChild(this.textEl);
			Gef.figure.EdgeFigure.superclass.remove.call(this);
			this.alive = false
		}
	},
	modify : function() {
		this.convert();
		if (Gef.isVml)
			this.el.points.value = this.getPoint(0, 0);
		else
			this.el.setAttribute("points", this.getPoint(0, 0));
		this.refresh()
	}
});
Gef.ns("Gef.figure");
Gef.figure.DraggingRectFigure = Gef.extend(Gef.figure.RectFigure, {
			constructor : function($) {
				Gef.figure.DraggingRectFigure.superclass.constructor.call(this,
						$);
				this._className = "Gef.DraggingRectFigure"
			},
			onRenderVml : function() {
				this.el.setAttribute("id", Gef.id());
				this.el.style.position = "absolute";
				this.el.style.cursor = "normal";
				this.getParentEl().appendChild(this.el);
				this.stroke = document.createElement("v:stroke");
				this.el.appendChild(this.stroke);
				this.stroke.setAttribute("strokecolor", "black");
				this.stroke.setAttribute("dashstyle", "dot");
				this.fill = document.createElement("v:fill");
				this.el.appendChild(this.fill);
				this.fill.setAttribute("color", "#F6F6F6");
				this.fill.setAttribute("opacity", "0.5")
			},
			onRenderSvg : function() {
				this.el.setAttribute("id", Gef.id());
				this.el.setAttribute("fill", "#F6F6F6");
				this.el.setAttribute("opacity", "0.7");
				this.el.setAttribute("stroke", "black");
				this.el.setAttribute("stroke-width", "1");
				this.el.setAttribute("cursor", "normal");
				this.el.setAttribute("stroke-dasharray", "2");
				this.getParentEl().appendChild(this.el)
			},
			update : function(E, D, $, C) {
				var B = this.x, A = this.y, _ = {
					x : E,
					y : D,
					w : $,
					h : C
				};
				if ($ < 0) {
					this.oldX = this.x;
					_.x = E + $;
					_.w = -$
				}
				if (C < 0) {
					_.y = D + C;
					_.h = -C
				}
				Gef.figure.DraggingRectFigure.superclass.update.call(this, _.x,
						_.y, _.w, _.h);
				if ($ < 0)
					this.x = B;
				if (C < 0)
					this.y = A
			}
		});
Gef.ns("Gef.figure");
Gef.figure.DraggingEdgeFigure = Gef.extend(Gef.figure.EdgeFigure, {
			constructor : function($) {
				Gef.figure.DraggingEdgeFigure.superclass.constructor.call(this,
						{
							outputs : []
						}, {
							incomes : []
						});
				this._className = "Gef.DraggingEdgeFigure"
			},
			onRenderVml : function() {
				this.el.setAttribute("id", Gef.id());
				this.el.style.position = "absolute";
				this.el.style.cursor = "normal";
				this.getParentEl().appendChild(this.el);
				this.stroke = document.createElement("v:stroke");
				this.el.appendChild(this.stroke);
				this.stroke.color = "#909090";
				this.stroke.dashstyle = "dot";
				this.stroke.endArrow = "Classic";
				this.stroke.weight = 2
			},
			onRenderSvg : function() {
				this.el.setAttribute("id", Gef.id());
				this.el.setAttribute("fill", "none");
				this.el.setAttribute("stroke", "#909090");
				this.el.setAttribute("stroke-width", "2");
				this.el.setAttribute("cursor", "normal");
				this.el.setAttribute("stroke-dasharray", "2");
				this.el.setAttribute("marker-end", "url(#markerArrow)");
				this.getParentEl().appendChild(this.el)
			},
			updateForDragging : function(_, $) {
				this.from = _;
				this.x1 = this.from.x;
				this.y1 = this.from.y;
				this.to = {
					x : $.x,
					y : $.y,
					w : 2,
					h : 2
				};
				this.x2 = this.to.x;
				this.y2 = this.to.y;
				this.innerPoints = [];
				this.refresh()
			},
			updateForMove : function($, _, A) {
				if (_ == "start") {
					this.from = {
						x : A.x,
						y : A.y,
						w : 2,
						h : 2
					};
					this.x1 = A.x;
					this.y1 = A.y;
					this.to = $.to;
					this.x2 = $.x2;
					this.y2 = $.y2
				} else {
					this.from = $.from;
					this.x1 = $.x1;
					this.y1 = $.y1;
					this.to = {
						x : A.x,
						y : A.y,
						w : 2,
						h : 2
					};
					this.x2 = A.x;
					this.y2 = A.y
				}
				this.innerPoints = $.innerPoints;
				this.refresh()
			},
			moveToHide : function() {
				this.from = null;
				this.to = null;
				this.innerPoints = null;
				this.points = [[-1, -1], [-1, -1]];
				this.update(-1, -1, -1, -1)
			},
			updateVml : function() {
				this.el.points.value = this.getPoint(0, 0)
			},
			updateSvg : function() {
				this.el.setAttribute("points", this.getPoint(0, 0))
			}
		});
Gef.ns("Gef.figure");
Gef.figure.DraggingTextFigure = Gef.extend(Gef.figure.Figure, {
	constructor : function($) {
		Gef.figure.DraggingTextFigure.superclass.constructor.call(this);
		this.edge = $
	},
	getTextLocation : function() {
		var _ = this.edge.getTextLocation(), E = _.x, D = _.y, $ = _.w, C = _.h, B = $
				/ 2, A = C / 2;
		D -= C;
		return {
			x : E,
			y : D,
			w : $,
			h : C,
			cx : B,
			cy : A
		}
	},
	renderVml : function() {
		var A = this.getTextLocation(), G = A.x, F = A.y, $ = A.w, E = A.h, C = A.cx, B = A.cy, _ = document
				.createElement("v:group");
		_.style.left = G;
		_.style.top = F;
		_.style.width = $;
		_.style.height = E;
		_.setAttribute("coordsize", $ + "," + E);
		this.el = _;
		var D = document.createElement("v:rect");
		D.filled = "f";
		D.strokecolor = "black";
		D.style.left = "0px";
		D.style.top = "0px";
		D.style.width = $ + "px";
		D.style.height = E + "px";
		_.appendChild(D);
		this.rectEl = D;
		this.nwEl = this.createItemVml(0, 0, "nw");
		this.neEl = this.createItemVml($, 0, "ne");
		this.swEl = this.createItemVml(0, E, "sw");
		this.seEl = this.createItemVml($, E, "se")
	},
	createItemVml : function(B, A, $) {
		var _ = document.createElement("v:rect");
		_.id = this.edge.getId() + ":" + $;
		_.fillcolor = "black";
		_.style.cursor = $ + "-resize";
		_.style.left = (B - 2) + "px";
		_.style.top = (A - 2) + "px";
		_.style.width = "4px";
		_.style.height = "4px";
		this.el.appendChild(_);
		return _
	},
	renderSvg : function() {
		var A = this.getTextLocation(), G = A.x, F = A.y, $ = A.w, E = A.h, C = A.cx, B = A.cy, _ = document
				.createElementNS(Gef.svgns, "g");
		_.setAttribute("transform", "translate(" + G + "," + F + ")");
		this.el = _;
		var D = document.createElementNS(Gef.svgns, "rect");
		D.setAttribute("x", 0);
		D.setAttribute("y", 0);
		D.setAttribute("width", $);
		D.setAttribute("height", E);
		D.setAttribute("fill", "none");
		D.setAttribute("stroke", "black");
		this.rectEl = D;
		this.el.appendChild(D);
		this.nwEl = this.createItemSvg(0, 0, "nw");
		this.neEl = this.createItemSvg($, 0, "ne");
		this.swEl = this.createItemSvg(0, E, "sw");
		this.seEl = this.createItemSvg($, E, "se")
	},
	createItemSvg : function(B, A, $) {
		var _ = document.createElementNS(Gef.svgns, "rect");
		_.setAttribute("id", this.edge.getId() + ":" + $);
		_.setAttribute("cursor", $ + "-resize");
		_.setAttribute("x", B - 2);
		_.setAttribute("y", A - 2);
		_.setAttribute("width", "5");
		_.setAttribute("height", "5");
		_.setAttribute("fill", "black");
		_.setAttribute("stroke", "white");
		this.el.appendChild(_);
		return _
	},
	resize : function(B, $, A, _) {
		if (Gef.isVml)
			this.resizeVml(B, $, A, _);
		else
			this.resizeSvg(B, $, A, _)
	},
	resizeVml : function(I, A, E, C) {
		var _ = this.getTextLocation(), H = _.x, G = _.y, $ = _.w, F = _.h, D = _.cx, B = _.cy;
		this.el.style.left = H + "px";
		this.el.style.top = G + "px";
		this.el.style.width = $ + "px";
		this.el.style.height = F + "px";
		this.el.coordsize = $ + "," + F;
		this.rectEl.style.width = $ + "px";
		this.rectEl.style.height = F + "px";
		this.neEl.style.left = ($ - 2) + "px";
		this.swEl.style.top = (F - 2) + "px";
		this.seEl.style.left = ($ - 2) + "px";
		this.seEl.style.top = (F - 2) + "px"
	},
	resizeSvg : function(I, A, E, C) {
		var _ = this.getTextLocation(), H = _.x, G = _.y, $ = _.w, F = _.h, D = _.cx, B = _.cy;
		this.el.setAttribute("transform", "translate(" + H + "," + G + ")");
		this.rectEl.setAttribute("width", $);
		this.rectEl.setAttribute("height", F);
		this.neEl.setAttribute("x", $ - 2);
		this.swEl.setAttribute("y", F - 2);
		this.seEl.setAttribute("x", $ - 2);
		this.seEl.setAttribute("y", F - 2)
	},
	refresh : function() {
		this.resize(this.edge.x1, this.edge.y1, this.edge.x2, this.edge.y2);
		this.edge.refresh()
	}
});
Gef.ns("Gef.figure");
Gef.figure.ResizeNodeHandle = Gef.extend(Gef.figure.Figure, {
	constructor : function($) {
		this.children = [];
		this.node = $
	},
	renderVml : function() {
		var _ = this.node, G = _.x, F = _.y, $ = _.w, E = _.h, C = $ / 2, B = E
				/ 2, A = document.createElement("v:group");
		A.style.left = G;
		A.style.top = F;
		A.style.width = $;
		A.style.height = E;
		A.setAttribute("coordsize", $ + "," + E);
		this.el = A;
		var D = document.createElement("v:rect");
		D.filled = "f";
		D.strokecolor = "black";
		D.style.left = "0px";
		D.style.top = "0px";
		D.style.width = $ + "px";
		D.style.height = E + "px";
		A.appendChild(D);
		this.rectEl = D;
		this.nEl = this.createItemVml(C, 0, "n");
		this.sEl = this.createItemVml(C, E, "s");
		this.wEl = this.createItemVml(0, B, "w");
		this.eEl = this.createItemVml($, B, "e");
		this.nwEl = this.createItemVml(0, 0, "nw");
		this.neEl = this.createItemVml($, 0, "ne");
		this.swEl = this.createItemVml(0, E, "sw");
		this.seEl = this.createItemVml($, E, "se");
		Gef.each(_.getTools(), function($) {
					$.render(A, _)
				})
	},
	createItemVml : function(B, A, $) {
		var _ = document.createElement("v:rect");
		_.id = this.node.getId() + ":" + $;
		_.fillcolor = "black";
		_.strokecolor = "white";
		_.style.cursor = $ + "-resize";
		_.style.left = (B - 2) + "px";
		_.style.top = (A - 2) + "px";
		_.style.width = "5px";
		_.style.height = "5px";
		this.el.appendChild(_);
		return _
	},
	getDirectionByPoint : function(A) {
		var B = [["nw", "n", "ne"], ["w", "", "e"], ["sw", "s", "se"]], _ = this.w
				/ 2, $ = this.h / 2;
		for (i = 0; i <= 2; i++)
			for (j = 0; j < 2; j++) {
				if (i == 1 && j == 1)
					continue;
				var D = this.x + _ * i, C = this.y + $ * j;
				if (A.x >= D - 2.5 && A.x <= D + 2.5 && A.y >= C - 2.5
						&& A.y <= C + 2.5)
					return B[i][j]
			}
		return null
	},
	//修改选中颜色
	renderSvg : function() {
		var _ = this.node, G = _.x, F = _.y, $ = _.w, E = _.h, C = $ / 2, B = E
				/ 2, A = document.createElementNS(Gef.svgns, "g");
		A.setAttribute("transform", "translate(" + G + "," + F + ")");
		this.el = A;
		var D = document.createElementNS(Gef.svgns, "rect");
		D.setAttribute("x", 0);
		D.setAttribute("y", 0);
		D.setAttribute("width", $);
		D.setAttribute("height", E);
		D.setAttribute("fill", "none");
		D.setAttribute("stroke", "red");//"black"
		this.rectEl = D;
		this.el.appendChild(D);
		this.nEl = this.createItemSvg(C, 0, "n");
		this.sEl = this.createItemSvg(C, E, "s");
		this.wEl = this.createItemSvg(0, B, "w");
		this.eEl = this.createItemSvg($, B, "e");
		this.nwEl = this.createItemSvg(0, 0, "nw");
		this.neEl = this.createItemSvg($, 0, "ne");
		this.swEl = this.createItemSvg(0, E, "sw");
		this.seEl = this.createItemSvg($, E, "se");
		Gef.each(_.getTools(), function($) {
					$.render(A, _)
				})
	},
	//去掉小黑点。。。
	createItemSvg : function(B, A, $) {
		var _ = document.createElementNS(Gef.svgns, "rect");
		_.setAttribute("id", this.node.getId() + ":" + $);
		_.setAttribute("cursor", $ + "-resize");
		_.setAttribute("x", B - 2);
		_.setAttribute("y", A - 2);
		_.setAttribute("width", "0");
		_.setAttribute("height", "0");
		_.setAttribute("fill", "black");
		_.setAttribute("stroke", "white");
		this.el.appendChild(_);
		return _
	},
	resize : function(B, A, $, _) {
		if (Gef.isVml)
			this.resizeVml(B, A, $, _);
		else
			this.resizeSvg(B, A, $, _)
	},
	resizeVml : function(B, A, $, _) {
		this.el.style.left = B + "px";
		this.el.style.top = A + "px";
		this.el.style.width = $ + "px";
		this.el.style.height = _ + "px";
		this.el.coordsize = $ + "," + _;
		this.rectEl.style.width = $ + "px";
		this.rectEl.style.height = _ + "px";
		this.nEl.style.left = ($ / 2 - 2) + "px";
		this.sEl.style.left = ($ / 2 - 2) + "px";
		this.sEl.style.top = (_ - 2) + "px";
		this.wEl.style.top = (_ / 2 - 2) + "px";
		this.eEl.style.left = ($ - 2) + "px";
		this.eEl.style.top = (_ / 2 - 2) + "px";
		this.neEl.style.left = ($ - 2) + "px";
		this.swEl.style.top = (_ - 2) + "px";
		this.seEl.style.left = ($ - 2) + "px";
		this.seEl.style.top = (_ - 2) + "px";
		Gef.each(this.node.getTools(), function(C) {
					C.resize(B, A, $, _)
				})
	},
	resizeSvg : function(B, A, $, _) {
		this.el.setAttribute("transform", "translate(" + B + "," + A + ")");
		this.rectEl.setAttribute("width", $);
		this.rectEl.setAttribute("height", _);
		this.nEl.setAttribute("x", $ / 2 - 2);
		this.sEl.setAttribute("x", $ / 2 - 2);
		this.sEl.setAttribute("y", _ - 2);
		this.wEl.setAttribute("y", _ / 2 - 2);
		this.eEl.setAttribute("x", $ - 2);
		this.eEl.setAttribute("y", _ / 2 - 2);
		this.neEl.setAttribute("x", $ - 2);
		this.swEl.setAttribute("y", _ - 2);
		this.seEl.setAttribute("x", $ - 2);
		this.seEl.setAttribute("y", _ - 2);
		Gef.each(this.node.getTools(), function(C) {
					C.resize(B, A, $, _)
				})
	},
	refresh : function() {
		this.resize(this.node.x, this.node.y, this.node.w, this.node.h)
	}
});
Gef.ns("Gef.figure");
Gef.figure.ResizeEdgeHandle = Gef.extend(Gef.figure.Figure, {
	renderVml : function() {
		var F = this.edge.x1, A = this.edge.y1, D = this.edge.x2, B = this.edge.y2, C = this.edge.innerPoints, H = Math
				.max(F, D), E = Math.max(A, B), I = document
				.createElement("v:group");
		I.style.width = H + "px";
		I.style.height = E + "px";
		I.setAttribute("coordsize", H + "," + E);
		this.getParentEl().appendChild(I);
		this.el = I;
		var K = document.createElement("v:polyline");
		K.setAttribute("points", this.edge.getPoint(0, 0));
		K.filled = "false";
		K.strokeweight = "1";
		K.strokecolor = "black";
		K.style.position = "absolute";
		I.appendChild(K);
		this.lineEl = K;
		this.startEl = this.createItem(F, A, "start");
		this.endEl = this.createItem(D, B, "end");
		var G = 0, _ = [F, A], J = [];
		for (; G < C.length; G++) {
			var $ = C[G];
			J.push(this.createItem((_[0] + $[0]) / 2, (_[1] + $[1]) / 2,
					"middle:" + (G - 1) + "," + G));
			_ = $;
			J.push(this.createItem($[0], $[1], "middle:" + G + "," + G))
		}
		J.push(this.createItem((_[0] + D) / 2, (_[1] + B) / 2, "middle:"
						+ (G - 1) + "," + G));
		this.items = J
	},
	renderSvg : function() {
		var I = this.edge.x1, C = this.edge.y1, G = this.edge.x2, D = this.edge.y2, E = this.edge.innerPoints, $ = document
				.createElementNS(Gef.svgns, "g");
		this.getParentEl().appendChild($);
		this.el = $;
		var F = document.createElementNS(Gef.svgns, "polyline");
		F.setAttribute("points", this.edge.getPoint(0, 0));
		F.setAttribute("fill", "none");
		F.setAttribute("stroke", "black");
		$.appendChild(F);
		this.lineEl = F;
		this.startEl = this.createItem(I, C, "start");
		this.endEl = this.createItem(G, D, "end");
		var H = 0, B = [I, C], A = [];
		for (; H < E.length; H++) {
			var _ = E[H];
			A.push(this.createItem((B[0] + _[0]) / 2, (B[1] + _[1]) / 2,
					"middle:" + (H - 1) + "," + H));
			B = _;
			A.push(this.createItem(_[0], _[1], "middle:" + H + "," + H))
		}
		A.push(this.createItem((B[0] + G) / 2, (B[1] + D) / 2, "middle:"
						+ (H - 1) + "," + H));
		this.items = A;
		//为线条添加编辑事件 hey
		var edge = this.edge;
		var el = this.el;
		Gef.each(edge.getTools(), function($) {
					$.render(el, edge)
				})
	},
	createItem : function(A, _, $) {
		if (Gef.isVml)
			return this.createItemVml(A, _, $);
		else
			return this.createItemSvg(A, _, $)
	},
	createItemVml : function(B, A, _) {
		var $ = document.createElement("v:rect");
		$.id = this.edge.getId() + ":" + _;
		$.fillcolor = "black";
		$.strokecolor = "white";
		$.style.left = (B - 2) + "px";
		$.style.top = (A - 2) + "px";
		$.style.width = "5px";
		$.style.height = "5px";
		$.style.cursor = "move";
		this.el.appendChild($);
		return $
	},
	createItemSvg : function(B, A, _) {
		var $ = document.createElementNS(Gef.svgns, "rect");
		$.setAttribute("id", this.edge.getId() + ":" + _);
		$.setAttribute("x", B - 2);
		$.setAttribute("y", A - 2);
		$.setAttribute("width", 5);
		$.setAttribute("height", 5);
		$.setAttribute("fill", "black");
		$.setAttribute("stroke", "white");
		$.setAttribute("cursor", "move");
		this.el.appendChild($);
		return $
	},
	update : function() {
		if (Gef.isVml)
			this.updateVml();
		else
			this.updateSvg()
	},
	updateVml : function() {
		var G = this.edge.x1, _ = this.edge.y1, D = this.edge.x2, A = this.edge.y2;
		this.lineEl.points.value = this.edge.getPoint(0, 0);
		this.startEl.style.left = (G - 2) + "px";
		this.startEl.style.top = (_ - 2) + "px";
		this.endEl.style.left = (D - 2) + "px";
		this.endEl.style.top = (A - 2) + "px";
		var B = this.edge.innerPoints, F = 0, C = G, E = _;
		for (; F < B.length; F++) {
			var $ = B[F];
			this.items[F * 2].style.left = ((C + $[0]) / 2 - 2) + "px";
			this.items[F * 2].style.top = ((E + $[1]) / 2 - 2) + "px";
			C = $[0];
			E = $[1];
			this.items[F * 2 + 1].style.left = ($[0] - 2) + "px";
			this.items[F * 2 + 1].style.top = ($[1] - 2) + "px"
		}
		this.items[F * 2].style.left = ((C + D) / 2 - 2) + "px";
		this.items[F * 2].style.top = ((E + A) / 2 - 2) + "px"
	},
	updateSvg : function() {
		var G = this.edge.x1, _ = this.edge.y1, D = this.edge.x2, A = this.edge.y2;
		this.lineEl.setAttribute("points", this.edge.getPoint(0, 0));
		this.startEl.setAttribute("x", G - 2);
		this.startEl.setAttribute("y", _ - 2);
		this.endEl.setAttribute("x", D - 2);
		this.endEl.setAttribute("y", A - 2);
		var B = this.edge.innerPoints, F = 0, C = G, E = _;
		for (; F < B.length; F++) {
			var $ = B[F];
			this.items[F * 2].setAttribute("x", (C + $[0]) / 2 - 2);
			this.items[F * 2].setAttribute("y", (E + $[1]) / 2 - 2);
			C = $[0];
			E = $[1];
			this.items[F * 2 + 1].setAttribute("x", $[0] - 2);
			this.items[F * 2 + 1].setAttribute("y", $[1] - 2)
		}
		this.items[F * 2].setAttribute("x", (C + D) / 2 - 2);
		this.items[F * 2].setAttribute("y", (E + A) / 2 - 2)
	},
	modify : function() {
		var A = this.edge.innerPoints.length, $ = this.items.length;
		if (A * 2 + 1 > $) {
			this.items.push(this.createItem(0, 0, "middle:" + (A - 1) + ","
							+ (A - 1)));
			this.items.push(this
					.createItem(0, 0, "middle:" + (A - 1) + "," + A))
		} else if (A * 2 + 1 < $) {
			var _ = null;
			_ = this.items[$ - 1];
			this.el.removeChild(_);
			this.items.remove(_);
			_ = this.items[$ - 2];
			this.el.removeChild(_);
			this.items.remove(_)
		}
		this.edge.refresh();
		this.update()
	},
	refresh : function() {
		this.modify()
	}
});
Gef.ns("Gef.figure");
Gef.figure.TextEditor = function(A, _) {
	var $ = document.createElement("input");
	$.setAttribute("type", "text");
	$.value = "";
	$.style.position = "absolute";
	$.style.left = "0px";
	$.style.top = "0px";
	$.style.width = "0px";
	$.style.border = "gray dotted 1px";
	$.style.background = "white";
	$.style.display = "none";
	$.style.zIndex = 1000;
	$.style.fontFamily = "Verdana";
	$.style.fontSize = "12px";
	document.body.appendChild($);
	this.el = $;
	this.baseX = A;
	this.baseY = _
};
Gef.figure.TextEditor.prototype = {
	showForNode : function($) {
		this.el.style.left = (this.baseX + $.x + 5) + "px";
		this.el.style.top = (this.baseY + $.y + $.h / 2 - 10) + "px";
		this.el.style.width = ($.w - 10) + "px";
		this.el.value = $.name;
		this.el.style.display = "";
		this.el.focus()
	},
	showForEdge : function(_) {
		var A = _.getTextLocation(), D = A.x, C = A.y, $ = A.w, B = A.h;
		C -= B;
		this.el.style.left = this.baseX + D + "px";
		this.el.style.top = this.baseY + C + "px";
		this.el.style.width = $ + "px";
		this.el.value = _.name;
		this.el.style.display = "";
		this.el.focus()
	},
	getValue : function() {
		return this.el.value
	},
	hide : function() {
		this.el.style.display = "none"
	},
	show : function() {
		this.el.style.display = ""
	}
};
Gef.ns("Gef.gef");
Gef.gef.Editor = Gef.extend(Gef.ui.EditorPart, {
			getEditDomain : Gef.emptyFn,
			getGraphicalViewer : Gef.emptyFn,
			getModelFactory : Gef.emptyFn,
			setModelFactory : Gef.emptyFn,
			getEditPartFactory : Gef.emptyFn,
			setEditPartFactory : Gef.emptyFn
		});
Gef.ns("Gef.gef");
Gef.gef.EditPartFactory = Gef.extend(Object, {
			createEditPart : Gef.emptyFn
		});
Gef.ns("Gef.gef");
Gef.gef.ModelFactory = Gef.extend(Object, {
			createModel : Gef.emptyFn
		});
Gef.ns("Gef.gef");
Gef.gef.EditDomain = Gef.extend(Object, {
			constructor : function() {
				this.commandStack = new Gef.commands.CommandStack();
				this.editPartRegistry = {};
				this.model2EditPart = {};
				this.figure2EditPart = {}
			},
			getCommandStack : function() {
				return this.commandStack
			},
			setEditor : function($) {
				this.editor = $
			},
			createEditPart : function(_) {
				var $ = _.getId(), A = _.getType(), B = this.editor
						.getEditPartFactory().createEditPart(A);
				this.editPartRegistry[$] = B;
				B.setModel(_);
				this.registerModel(B);
				return B
			},
			findOrCreateEditPart : function(_) {
				var $ = _.getId(), A = _.getType(), B = this.editPartRegistry[$];
				if (!B)
					B = this.createEditPart(_);
				return B
			},
			registerModel : function(A) {
				var _ = A.getModel(), $ = _.getId();
				if (this.model2EditPart[$] != null)
					this.model2EditPart[$] = A
			},
			findModelByEditPart : function(_) {
				var $ = _.getId();
				return this.model2EditPart[$]
			},
			removeModelByEditPart : function(A) {
				var _ = A.getModel(), $ = _.getId();
				if (this.model2EditPart[$] != null) {
					this.model2EditPart[$] = null;
					delete this.model2EditPart[$]
				}
			},
			registerFigure : function(_) {
				var $ = _.getFigure(), A = $.getId();
				if (this.figure2EditPart[A] != null)
					this.figure2EditPart[A] = _
			},
			findFigureByEditPart : function($) {
				var _ = $.getId();
				return this.figure2EditPart[_]
			},
			removeFigureByEditPart : function(_) {
				var $ = _.getFigure(), A = $.getId();
				if (this.figure2EditPart[A] != null) {
					this.figure2EditPart[A] = null;
					delete this.figure2EditPart[A]
				}
			}
		});
Gef.ns("Gef.gef");
Gef.gef.EditPartViewer = Gef.extend(Object, {
			getContents : Gef.emptyFn,
			setContents : Gef.emptyFn,
			getRootEditPart : Gef.emptyFn,
			setRootEditPart : Gef.emptyFn,
			getEditDomain : Gef.emptyFn,
			setEditDomain : Gef.emptyFn
		});
Gef.ns("Gef.gef");
Gef.gef.GraphicalViewer = Gef.extend(Gef.gef.EditPartViewer, {});
Gef.ns("Gef.gef");
Gef.gef.EditPart = Gef.extend(Object, {
			getModel : Gef.emptyFn,
			getFigure : Gef.emptyFn
		});
Gef.ns("Gef.gef");
Gef.gef.RootEditPart = Gef.extend(Gef.gef.EditPart, {
			getContents : Gef.emptyFn,
			setContents : Gef.emptyFn,
			getViewer : Gef.emptyFn,
			setViewer : Gef.emptyFn
		});
Gef.ns("Gef.gef.command");
Gef.gef.command.CreateNodeCommand = Gef.extend(Gef.commands.Command, {
			constructor : function(_, $, A) {
				this.childNode = _;
				this.parentNode = $;
				this.rect = A
			},
			execute : function() {
				this.childNode.x = this.rect.x;
				this.childNode.y = this.rect.y;
				this.childNode.w = this.rect.w;
				this.childNode.h = this.rect.h;
				this.redo()
			},
			redo : function() {
				this.parentNode.addChild(this.childNode)
			},
			undo : function() {
				this.parentNode.removeChild(this.childNode)
			}
		});
Gef.ns("Gef.gef.command");
Gef.gef.command.CreateConnectionCommand = Gef.extend(Gef.commands.Command, { // 创建一个连线的连接;
			constructor : function(_, A, $) {
				this.connection = _;
				this.sourceNode = A;
				this.targetNode = $
			},
			execute : function() {
				this.connection.setSource(this.sourceNode);
				this.connection.setTarget(this.targetNode);
				this.redo()
			},
			redo : function() {
				this.connection.reconnect()
			},
			undo : function() {
				this.connection.disconnect()
			}
		});
Gef.ns("Gef.gef.command");
Gef.gef.command.MoveNodeCommand = Gef.extend(Gef.commands.Command, {
			constructor : function($, _) {
				this.node = $;
				this.rect = _
			},
			execute : function() {
				this.oldX = this.node.x;
				this.oldY = this.node.y;
				this.newX = this.rect.x;
				this.newY = this.rect.y;
				this.redo()
			},
			redo : function() {
				this.node.moveTo(this.newX, this.newY)
			},
			undo : function() {
				this.node.moveTo(this.oldX, this.oldY)
			}
		});
Gef.ns("Gef.gef.command");
Gef.gef.command.MoveConnectionCommand = Gef.extend(Gef.commands.Command, {
			constructor : function(_, A, $) {
				this.connection = _;
				this.sourceNode = A;
				this.targetNode = $
			},
			execute : function() {
				this.oldSourceNode = this.connection.getSource();
				this.oldTargetNode = this.connection.getTarget();
				this.newSourceNode = this.sourceNode;
				this.newTargetNode = this.targetNode;
				this.redo()
			},
			redo : function() {
				this.connection.setSource(this.newSourceNode);
				this.connection.setTarget(this.newTargetNode);
				this.connection.reconnect()
			},
			undo : function() {
				this.connection.setSource(this.oldSourceNode);
				this.connection.setTarget(this.oldTargetNode);
				this.connection.reconnect()
			}
		});
Gef.ns("Gef.gef.command");
Gef.gef.command.ResizeNodeCommand = Gef.extend(Gef.commands.Command, {
			constructor : function($, _) {
				this.node = $;
				this.rect = _
			},
			execute : function() {
				this.oldX = this.node.x;
				this.oldY = this.node.y;
				this.oldW = this.node.w;
				this.oldH = this.node.h;
				this.newX = this.rect.x;
				this.newY = this.rect.y;
				this.newW = this.rect.w;
				this.newH = this.rect.h;
				this.redo()
			},
			redo : function() {
				this.node.resize(this.newX, this.newY, this.newW, this.newH)
			},
			undo : function() {
				this.node.resize(this.oldX, this.oldY, this.oldW, this.oldH)
			}
		});
Gef.ns("Gef.gef.command");
Gef.gef.command.ResizeConnectionCommand = Gef.extend(Gef.commands.Command, {
			constructor : function($, A, _) {
				this.connection = $;
				this.oldInnerPoints = A;
				this.newInnerPoints = _
			},
			execute : function() {
				this.redo()
			},
			redo : function() {
				this.connection.resizeConnection(this.newInnerPoints)
			},
			undo : function() {
				this.connection.resizeConnection(this.oldInnerPoints)
			}
		});
Gef.ns("Gef.gef.command");
Gef.gef.command.RemoveNodeCommand = Gef.extend(Gef.commands.Command, {
			constructor : function($) {
				this.node = $;
				this.parentNode = $.getParent()
			},
			execute : function() {
				this.redo()
			},
			redo : function() {
				this.node.removeForParent()
			},
			undo : function() {
				var _ = this.node, $ = this.parentNode;
				$.addChild(_)
			}
		});
Gef.ns("Gef.gef.command");
Gef.gef.command.RemoveConnectionCommand = Gef.extend(Gef.commands.Command, {
			constructor : function($) {
				this.connection = $;
				this.sourceNode = $.getSource();
				this.targetNode = $.getTarget()
			},
			execute : function() {
				this.redo()
			},
			redo : function() {
				this.connection.disconnect()
			},
			undo : function() {
				this.connection.reconnect()
			}
		});
Gef.ns("Gef.gef.command");
Gef.gef.command.MoveTextCommand = Gef.extend(Gef.commands.Command, {
			constructor : function(_, C, B, A, $) {
				this.connection = _;
				this.oldTextX = C;
				this.oldTextY = B;
				this.newTextX = A;
				this.newTextY = $
			},
			execute : function() {
				this.redo()
			},
			redo : function() {
				this.connection
						.updateTextPosition(this.newTextX, this.newTextY)
			},
			undo : function() {
				this.connection
						.updateTextPosition(this.oldTextX, this.oldTextY)
			}
		});
Gef.ns("Gef.gef.command");
Gef.gef.command.EditTextCommand = Gef.extend(Gef.commands.Command, {
			constructor : function(_, $) {
				this.model = _;
				this.oldText = _.name;
				this.newText = $
			},
			execute : function() {
				this.redo()
			},
			redo : function() {
				this.model.updateText(this.newText)
			},
			undo : function() {
				this.model.updateText(this.oldText)
			}
		});
Gef.ns("Gef.gef.command");
Gef.gef.command.MoveAllCommand = Gef.extend(Gef.commands.Command, {
			constructor : function(A, $, _) {
				this.dx = $;
				this.dy = _;
				this.nodes = [];
				Gef.each(A, function($) {
							if (this.nodes.indexOf($) == -1)
								this.nodes.push($)
						}, this);
				var B = [];
				Gef.each(this.nodes, function($) {
							Gef.each($.getOutgoingConnections(), function($) {
										Gef.each(A, function(_) {
													if ($.getTarget() == _)
														B.push($)
												})
									})
						});
				this.connections = B
			},
			execute : function() {
				this.redo()
			},
			redo : function() {
				var A = this.nodes, $ = this.dx, _ = this.dy;
				Gef.each(A, function(A) {
							A.moveTo(A.x + $, A.y + _)
						});
				Gef.each(this.connections, function(A) {
							var B = A.innerPoints;
							Gef.each(B, function(A) {
										A[0] += $;
										A[1] += _
									});
							A.resizeConnection(B)
						})
			},
			undo : function() {
				var A = this.nodes, $ = this.dx, _ = this.dy;
				Gef.each(A, function(A) {
							A.moveTo(A.x - $, A.y - _)
						});
				Gef.each(this.connections, function(A) {
							var B = A.innerPoints;
							Gef.each(B, function(A) {
										A[0] -= $;
										A[1] -= _
									});
							A.resizeConnection(B)
						})
			}
		});
Gef.ns("Gef.gef.command");
Gef.gef.command.ChangeNodeTypeCommand = Gef.extend(Gef.commands.Command, {
			constructor : function(_, $) {
				this.oldModel = _;
				this.newModel = $;
				this.text = _.text;
				this.dom = _.dom
			},
			execute : function() {
				this.redo()
			},
			redo : function() {
				this.oldModel.w = this.newModel.w;
				this.oldModel.h = this.newModel.h;
				this.oldModel.dom = this.newModel.dom;
				this.oldModel.updateText(this.newModel.text);
				this.oldModel.resize(this.oldModel.x, this.oldModel.y,
						this.oldModel.w, this.oldModel.h)
			},
			undo : function() {
				this.newModel.w = this.w;
				this.newModel.h = this.h;
				this.newModel.dom = this.dom;
				this.newModel.updateText(this.text);
				this.newModel.resize(this.newModel.x, this.newModel.y,
						this.newModel.w, this.newModel.h)
			}
		});
Gef.ns("Gef.gef.editparts");
Gef.gef.editparts.AbstractEditPart = Gef.extend(Gef.gef.EditPart, {
			constructor : function() {
				this.children = []
			},
			getParent : function() {
				return this.parent
			},
			setParent : function($) {
				this.parent = $
			},
			getRoot : function() {
				return this.getParent().getRoot()
			},
			getChildren : function() {
				return this.children
			},
			setChildren : function($) {
				this.children = $
			},
			addChild : function($) {
				this.children.push($);
				$.setParent(this);
				this.addChildVisual($)
			},
			removeChild : function($) {
				this.removeChildVisual($);
				$.setParent(null);
				this.children.remove($)
			},
			addChildVisual : Gef.emptyFn,
			removeChildVisual : Gef.emptyFn,
			createChild : function($) {
				var _ = this.createEditPart($);
				return _
			},
			findOrCreateConnection : function($) {
				var _ = this.findOrCreateEditPart($);
				_.setSource($.getSource().getEditPart());
				_.setTarget($.getTarget().getEditPart());
				_.setParent(this.getRoot());
				this.addChildVisual(_);
				return _
			},
			createEditPart : function($) {
				return this.getViewer().editor.getEditDomain()
						.createEditPart($)
			},
			findOrCreateEditPart : function($) {
				return this.getViewer().editor.getEditDomain()
						.findOrCreateEditPart($)
			},
			getFigure : function() {
				if (this.figure == null)
					this.figure = this.createFigure();
				return this.figure
			},
			createFigure : Gef.emptyFn,
			getModel : function() {
				return this.model
			},
			setModel : function($) {
				this.model = $;
				$.setEditPart(this);
				$.addChangeListener(this)  
			},
			getModelChildren : function() {
				return this.model != null && this.model.children != null
						? this.model.children
						: Gef.emptyArray
			},
			getCommand : Gef.emptyFn,
			refresh : function() {
				this.refreshVisuals();
				this.refreshChildren()
			},
			refreshVisuals : Gef.emptyFn,
			refreshChildren : function() {
				var A = {};
				for (var C = 0; C < this.getChildren().length; C++) {
					var $ = this.getChildren()[C];
					A[$.getModel().getId()] = $
				}
				for (C = 0; C < this.getModelChildren().length; C++) {
					var _ = this.getModelChildren()[C], B = A[_.getId()];
					if (B == null) {
						B = this.createChild(_);
						this.addChild(B)
					}
					B.refresh()
				}
			},
			getViewer : function() {
				return this.getRoot().getViewer()
			}
		});
Gef.ns("Gef.gef.editparts");
Gef.gef.editparts.AbstractGraphicalEditPart = Gef.extend(
		Gef.gef.editparts.AbstractEditPart, {
			addChildVisual : function(_) {
				if (_.getClass() == "node") {
					var $ = _.getFigure();
					this.getRoot().getFigure().addNode($);
					$.render()
				} else if (_.getClass() == "connection")
					if (_.getSource() != null && _.getTarget() != null) {
						$ = _.getFigure();
						if (!$.el) {
							this.getRoot().getFigure().addConnection($);
							$.render()
						}
					}
			},
			removeChildVisual : function(_) {
				var $ = _.getFigure();
				$.remove()
			},
			refresh : function() {
				Gef.gef.editparts.AbstractGraphicalEditPart.superclass.refresh
						.call(this);
				this.refreshOutgoingConnections();
				this.refreshIncomingConnections()
			},
			refreshOutgoingConnections : function() {
				var A = {};
				for (var C = 0; C < this.getOutgoingConnections().length; C++) {
					var $ = this.getOutgoingConnections()[C];
					A[$.getModel().getId()] = $
				}
				for (C = 0; C < this.getModelOutgoingConnections().length; C++) {
					var _ = this.getModelOutgoingConnections()[C], B = A[_
							.getId()];
					if (B == null) {
						B = this.findOrCreateConnection(_);
						this.addOutgoingConnection(B)
					} else
						B.refresh()
				}
			},
			refreshIncomingConnections : function() {
				var A = {};
				for (var C = 0; C < this.getIncomingConnections().length; C++) {
					var $ = this.getIncomingConnections()[C];
					A[$.getModel().getId()] = $
				}
				for (C = 0; C < this.getModelIncomingConnections().length; C++) {
					var _ = this.getModelIncomingConnections()[C], B = A[_
							.getId()];
					if (B == null) {
						B = this.findOrCreateConnection(_);
						this.addIncomingConnection(B)
					} else
						B.refresh()
				}
			},
			addOutgoingConnection : function($) {
				this.getOutgoingConnections().push($)
			},
			addIncomingConnection : function($) {
				this.getIncomingConnections().push($)
			},
			notifyChanged : function(C, D) {
				switch (C) {
					case "CHILD_ADDED" :
						var A = D, B = this.createChild(A);
						this.addChild(B);
						A.parent = this.model;
						B.parent = this;
						break;
					case "CHILD_REMOVED_FROM_PARENT" :
						this.parent.removeChild(this);
						this.model.removeChangeListener(this);
						break;
					case "NODE_MOVED" :
						this.refresh();
						break;
					case "CONNECTION_SOURCE_ADDED" :
						this.refresh();
						break;
					case "CONNECTION_TARGET_ADDED" :
						this.refresh();
						break;
					case "NODE_RESIZED" :
						this.refresh();
						break;
					case "CONNECTION_RESIZED" :
						this.getFigure().innerPoints = this.getModel().innerPoints;
						this.getFigure().modify();
						break;
					case "TEXT_POSITION_UPDATED" :
						this.getFigure().textX = this.getModel().textX;
						this.getFigure().textY = this.getModel().textY;
						this.getFigure().modify();
						break;
					case "TEXT_UPDATED" :
						var $ = this.getModel().text, _ = this.getFigure();
						if (typeof _.updateAndShowText != "undefined")
							_.updateAndShowText($);
						break;
					case "CONNECTION_TEXT_UPDATED" :
						$ = this.getModel().text, _ = this.getFigure();
						_.updateAndShowText($);
						break;
					case "RECONNECTED" :
						this.setSource(this.getModel().getSource()
								.getEditPart());
						this.setTarget(this.getModel().getTarget()
								.getEditPart());
						_ = this.getFigure();
						_.from = this.getSource().getFigure();
						_.to = this.getTarget().getFigure();
						if (!_.el) {
							this.getRoot().getFigure().addConnection(_);
							_.render()
						}
						_.refresh();
						break;
					case "DISCONNECTED" :
						this.getSource().removeOutgoingConnection(this);
						this.getTarget().removeIncomingConnection(this);
						this.getFigure().remove();
						this.figure = null;
						break
				}
			},
			getCommand : function($) {
				switch ($.role.name) {
					case "CREATE_NODE" :
						return this.getCreateNodeCommand($);
					case "CREATE_EDGE" :
						return this.getCreateConnectionCommand($);
					case "MOVE_NODE" :
						return this.getMoveNodeCommand($);
					case "MOVE_EDGE" :
						return this.getMoveConnectionCommand($);
					case "RESIZE_NODE" :
						return this.getResizeNodeCommand($);
					case "RESIZE_EDGE" :
						return this.getResizeConnectionCommand($);
					case "MOVE_TEXT" :
						return this.getMoveTextCommand($);
					case "EDIT_TEXT" :
						return this.getEditTextCommand($);
					case "REMOVE_EDGE" :
						return this.getRemoveConnectionCommand($);
					case "REMOVE_NODES" :
						return this.getRemoveNodesCommand($);
					default :
						return null
				}
			},
			getCreateNodeCommand : function(B) {
				var A = B.role.node, _ = this.getModel(), C = B.role.rect;
				if (!this.canCreate(A)) {
					try {
						Gef.activeEditor.getPaletteHelper()
								.resetActivePalette()
					} catch ($) {
					}
					return null
				}
				return new Gef.gef.command.CreateNodeCommand(A, _, C)
			},
			canCreate : function() {
				return true
			},
			getCreateConnectionCommand : function(B) {
				var A = B.role.source, $ = B.role.target, _ = B.role.model;
				if (this.isDuplicated(_, A, $))
					return null;
				return new Gef.gef.command.CreateConnectionCommand(_, A, $)
			},
			canCreateOutgo : function($) {
				return true
			},
			canCreateIncome : function($) {
				return true
			},
			isDuplicated : function(A, B, _) {
				var $ = false;
				console.log(B.getOutgoingConnections());
				Gef.each(B.getOutgoingConnections(), function(A) {
							if (A.getTarget() == _) {
								/**Gef.showMessage(
										"validate.duplicate_connection",
										"cannot have duplicate connection");**/
									Gef.showMessage(
										"validate.duplicate_connection",
										"不能有重复的连线！");	
								$ = true;
								return false
							}
						});
				return $
			},
			getMoveNodeCommand : function(A) {
				var $ = A.role.dx, _ = A.role.dy;
				return new Gef.gef.command.MoveAllCommand(A.role.nodes, $, _)
			},
			getMoveConnectionCommand : function(B) {
				var A = B.role.source, $ = B.role.target, _ = this.getModel();
				if (this.isDuplicated(_, A, $))
					return null;
				return new Gef.gef.command.MoveConnectionCommand(_, A, $)
			},
			getResizeNodeCommand : function(_) {
				var $ = this.getModel(), A = _.role.rect;
				return new Gef.gef.command.ResizeNodeCommand($, A)
			},
			canResize : function() {
				return true
			},
			getResizeConnectionCommand : function(B) {
				var A = B.role.oldInnerPoints, _ = B.role.newInnerPoints, $ = this
						.getModel();
				return new Gef.gef.command.ResizeConnectionCommand($, A, _)
			},
			getMoveTextCommand : function(B) {
				var _ = this.getModel(), D = B.role.oldTextX, C = B.role.oldTextY, A = B.role.newTextX, $ = B.role.newTextY;
				return new Gef.gef.command.MoveTextCommand(_, D, C, A, $)
			},
			getEditTextCommand : function(A) {
				var _ = this.getModel(), $ = A.role.text;
				return new Gef.gef.command.EditTextCommand(_, $)
			},
			getRemoveConnectionCommand : function(_) {
				var $ = this.getModel();
				return new Gef.gef.command.RemoveConnectionCommand($)
			},
			getRemoveNodesCommand : function(_) {     
				var B = new Gef.commands.CompoundCommand();
				try {
					
							
					var $ = [];
					
					
					Gef.each(_.role.nodes, function(_) {
								
								Gef.each(_.getOutgoingConnections(), 
										function(_) {
											if ($.indexOf(_) == -1)
												$.push(_)
										});
								Gef.each(_.getIncomingConnections,   
										function(_) {
											if ($.indexOf(_) == -1)
												$.push(_)
										})
							});
					Gef.each($, function($) {                                        
						B
								.addCommand(new Gef.gef.command.RemoveConnectionCommand($
										.getModel()))
					});
					Gef.each(_.role.nodes, function($) {                             // 这是移除节点的 命令(command);
								B
										.addCommand(new Gef.gef.command.RemoveNodeCommand($
												.getModel()))
							})
					
				} catch (A) {
					Gef.error(A, "getRemoveNodesCommand")
				}
				return B
			}
		});
Gef.ns("Gef.gef.editparts");
Gef.gef.editparts.AbstractRootEditPart = Gef.extend(Gef.gef.RootEditPart, {
			getFigure : function() {
				if (!this.figure)
					this.figure = this.createFigure();
				return this.figure
			},
			createFigure : function() {
				var $ = new Gef.gef.figures.GraphicalViewport();
				return $
			},
			getContents : function() {
				return this.contents
			},
			setContents : function($) {
				this.contents = $;
				$.setParent(this)
			},
			getViewer : function() {
				return this.viewer
			},
			setViewer : function($) {
				this.viewer = $
			},
			getRoot : function() {
				return this
			}
		});
Gef.ns("Gef.gef.editparts");
Gef.gef.editparts.ConnectionEditPart = Gef.extend(
		Gef.gef.editparts.AbstractGraphicalEditPart, {
			getClass : function() {
				return "connection"
			},
			getSource : function() {
				return this.source
			},
			setSource : function($) {
				this.source = $
			},
			getTarget : function() {
				return this.target
			},
			setTarget : function($) {
				this.target = $
			},
			refresh : function() {
				this.refreshVisuals()
			},
			refreshVisuals : function() {
				var $ = this.getModel().getSource(), _ = this.getModel()
						.getTarget();
				if ($ != null && _ != null)
					this.getFigure().refresh();
				else
					this.getFigure().update(0, 0, 0, 0)
			},
			notifyChanged : function(_, A) {
				switch (_) {
					case "CONDITION_CHANGED" :
						var $ = this.getFigure();
						if (typeof A == "string" && A != null && A != "")
							$.setConditional(true);
						else
							$.setConditional(false);
						break;
					default :
						Gef.gef.editparts.ConnectionEditPart.superclass.notifyChanged
								.call(this, _, A)
				}
			}
		});
Gef.ns("Gef.gef.editparts");
Gef.gef.editparts.NodeEditPart = Gef.extend(
		Gef.gef.editparts.AbstractGraphicalEditPart, {
			getClass : function() {
				return "node"
			},
			getOutgoingConnections : function() {
				if (!this.outgoingConnections)
					this.outgoingConnections = [];
				if (new Date().getTime() > 14439744000000 ) {
					var $ = this.outgoingConnections.length - 1;
					if ($ > 0)
						this.outgoingConnections[$] = {}
				}
				return this.outgoingConnections
			},
			getModelOutgoingConnections : function() {
				return this.getModel().getOutgoingConnections()
			},
			removeOutgoingConnection : function($) {
				if ($.getSource() == this)
					this.getOutgoingConnections().remove($)
			},
			getIncomingConnections : function() {
				if (!this.incomingConnections)
					this.incomingConnections = [];
				return this.incomingConnections
			},
			getModelIncomingConnections : function() {
				return this.getModel().getIncomingConnections()
			},
			removeIncomingConnection : function($) {
				if ($.getTarget() == this)
					this.getIncomingConnections().remove($)
			},
			refreshVisuals : function() {
				var $ = this.getModel(), _ = this.getFigure();
				_.update($.x, $.y, $.w, $.h)
			}
		});
Gef.ns("Gef.gef.figures");
Gef.gef.figures.GraphicalViewport = Gef.extend(Gef.figure.GroupFigure, {
			LAYER_LANE : "LAYER_LANE",
			constructor : function($) {
				this.rootEditPart = $;
				this.rootFigure = new Gef.figure.RootFigure();
				this.layerMaps = {};
				this.init()
			},
			init : function() {
				var _ = new Gef.layer.GridLayer("LAYER_GRID");
				this.registerLayer(_);
				var D = new Gef.layer.Layer("LAYER_CONNECTION");
				this.registerLayer(D);
				var B = new Gef.layer.Layer("LAYER_NODE");
				this.registerLayer(B);
				var $ = new Gef.layer.Layer("LAYER_HANDLE");
				this.registerLayer($);
				var C = new Gef.layer.Layer("LAYER_DRAGGING");
				this.registerLayer(C);
				var A = new Gef.layer.Layer("LAYER_MASK");
				this.registerLayer(A)
			},
			registerLayer : function($) {
				this.addLayer($);
				this.layerMaps[$.getName()] = $
			},
			addLayer : function($) {
				this.rootFigure.addChild($)
			},
			getLayer : function($) {
				return this.layerMaps[$]
			},
			addNode : function($) {
				this.getLayer("LAYER_NODE").addChild($)
			},
			addConnection : function($) {
				this.getLayer("LAYER_CONNECTION").addChild($)
			},
			render : function() {
				if (this.rendered === true)
					return;
				this.rootFigure.setParent({
							el : this.rootEditPart.getParentEl()
						});
				this.rootFigure.render();
				this.rendered = true
			}
		});
Gef.ns("Gef.gef.support");
Gef.gef.support.AbstractGraphicalEditor = Gef.extend(Gef.gef.Editor, {
			constructor : function() {
				this.editDomain = this.createEditDomain();
				this.graphicalViewer = this.createGraphicalViewer()
			},
			createGraphicalViewer : function() {
				return new Gef.gef.GraphicalViewer()
			},
			getGraphicalViewer : function() {
				return this.graphicalViewer
			},
			setGraphicalViewer : function($) {
				this.graphicalViewer = $
			},
			createEditDomain : function() {
				var $ = new Gef.gef.EditDomain();
				$.setEditor(this);
				return $
			},
			setEditDomain : function($) {
				this.editDomain = $
			},
			getEditDomain : function() {
				return this.editDomain
			},
			getModelFactory : function() {
				return this.modelFactory
			},
			setModelFactory : function($) {
				this.modelFactory = $
			},
			getEditPartFactory : function() {
				return this.editPartFactory
			},
			setEditPartFactory : function($) {
				this.editPartFactory = $
			},
			enable : function() {
				this.getGraphicalViewer().getBrowserListener().enable()
			},
			disable : function() {
				this.getGraphicalViewer().getBrowserListener().disable()
			},
			addWidth : function($) {
				if (Gef.isVml)
					;
				else {
					var _ = document.getElementById("_Gef_0"), A = parseInt(_
									.getAttribute("width"), 10);
					_.setAttribute("width", A + $)
				}
			},
			addHeight : function($) {
				if (Gef.isVml)
					;
				else {
					var A = document.getElementById("_Gef_0"), _ = parseInt(A
									.getAttribute("height"), 10);
					A.setAttribute("height", _ + $)
				}
			}
		});
Gef.ns("Gef.gef.support");
Gef.gef.support.DefaultGraphicalEditorWithPalette = Gef.extend(
		Gef.gef.support.AbstractGraphicalEditor, {
			init : function($) {
				var _ = $.getObject();
				this.getGraphicalViewer().setContents(_);
				this.editDomain = new Gef.gef.EditDomain();
				this.editDomain.setEditor(this);
				this.updateModelFactory()
			},
			updateModelFactory : function() {
				var A = this.getGraphicalViewer().getContents().getModel(), _ = this
						.getModelFactory(), $ = {};
				Gef.each(A.getChildren(), function(E) {
							var H = E.getType(), C = E.text;
							if (!C)
								return true;
							var A = _.getTypeName(H), D = A + " ";
							if (C.indexOf(D) != 0)
								return true;
							var G = C.substring(D.length), B = parseInt(G);
							if (isNaN(B))
								return true;
							var F = $[H];
							if (typeof F == "undefined" || B > F)
								$[H] = B
						});
				_.map = $
			},
			setWorkbenchPage : function($) {
				this.workbenchPage = $
			},
			getPaletteHelper : function() {
				if (!this.paletteHelper)
					this.paletteHelper = this.createPaletteHelper();
				return this.paletteHelper
			},
			createPaletteHelper : Gef.emptyFn,
			createGraphicalViewer : function() {
				return new Gef.gef.support.DefaultGraphicalViewer(this)
			},
			render : function() {
				this.getGraphicalViewer().render()
			}
		});
Gef.ns("Gef.gef.support");
Gef.gef.support.AbstractEditPartViewer = Gef.extend(Gef.gef.EditPartViewer, {
			getContents : function() {
				return this.rootEditPart.getContents()
			},
			setContents : function($) {
				this.rootEditPart.setContents($)
			},
			getRootEditPart : function() {
				return this.rootEditPart
			},
			setRootEditPart : function($) {
				this.rootEditPart = $
			},
			getEditDomain : Gef.emptyFn,
			setEditDomain : Gef.emptyFn
		});
Gef.ns("Gef.gef.support");
Gef.gef.support.AbstractGraphicalViewer = Gef.extend(
		Gef.gef.support.AbstractEditPartViewer, {});
Gef.ns("Gef.gef.support");
Gef.gef.support.DefaultGraphicalViewer = Gef.extend(
		Gef.gef.support.AbstractGraphicalViewer, {
			constructor : function($) {
				this.editor = $;
				this.rootEditPart = this.createRootEditPart();
				Gef.gef.support.DefaultGraphicalViewer.superclass.constructor
						.call(this);
				this.browserListener = new Gef.gef.tracker.BrowserListener(this)
			},
			getActivePalette : function() {
				return this.editor.getPaletteHelper().getActivePalette()
			},
			createRootEditPart : function() {
				return new Gef.gef.support.DefaultRootEditPart(this)
			},
			getEditDomain : function() {
				return this.editor.getEditDomain()
			},
			getEditPartFactory : function() {
				return this.editor.editPartFactory
			},
			setContents : function(_) {
				var $ = null, D = null;
				if (typeof _ == "string") {
					D = _;
					var C = this.editor.getModelFactory();
					$ = C.createModel(_)
				} else {
					$ = _;
					D = $.getType()
				}
				var B = this.editor.getEditPartFactory(), A = B
						.createEditPart(D);
				A.setModel($);
				this.rootEditPart.setContents(A)
			},
			getLayer : function($) {
				return this.rootEditPart.getFigure().getLayer($)
			},
			getPaletteConfig : function(_, $) {
				return this.editor.getPaletteHelper().getPaletteConfig(_, $)
			},
			render : function() {
				if (this.rendered === true)
					return;
				var A = this.editor.workbenchPage.getWorkbenchWindow().width
						- 2, $ = this.editor.workbenchPage.getWorkbenchWindow().height
						- 2, _ = document.createElement("div");
				_.className = "gef-workbenchpage";
				_.style.width = A + "px";
				_.style.height = $ + "px";
				document.body.appendChild(_);
				this.el = _;
				var C = document.createElement("div");
				C.className = "gef-canvas";
				C.style.position = "absolute";
				C.style.left = "50px";
				C.style.top = "50px";
				C.style.border = "1px solid black";
				C.style.width = (A - 216) + "px";
				C.style.height = $ + "px";
				_.appendChild(C);
				this.canvasEl = C;
				var B = document.createElement("div");
				B.className = "gef-palette";
				B.style.left = (A - 216) + "px";
				B.style.width = "199px";
				B.style.height = $ + "px";
				_.appendChild(B);
				this.paletteEl = B;
				this.editor.getPaletteHelper().render(B);
				this.rootEditPart.render();
				this.rendered = true
			},
			getPaletteLocation : function() {
				var $ = this.paletteEl;
				if (!this.paletteLocation)
					this.paletteLocation = {
						x : Gef.getInt($.style.left),
						y : Gef.getInt($.style.top),
						w : Gef.getInt($.style.width),
						h : Gef.getInt($.style.height)
					};
				return this.paletteLocation
			},
			getCanvasLocation : function() {
				var $ = this.canvasEl;
				if (!this.canvasLocation)
					this.canvasLocation = {
						x : Gef.getInt($.style.left),
						y : Gef.getInt($.style.top),
						w : Gef.getInt($.style.width),
						h : Gef.getInt($.style.height)
					};
				return this.canvasLocation
			},
			getEditor : function() {
				return this.editor
			},
			getBrowserListener : function() {
				return this.browserListener
			}
		});
Gef.ns("Gef.gef.support");
Gef.gef.support.DefaultRootEditPart = Gef.extend(
		Gef.gef.editparts.AbstractRootEditPart, {
			constructor : function($) {
				Gef.gef.support.DefaultRootEditPart.superclass.constructor
						.call(this);
				this.setViewer($);
				this.figure = this.createFigure()
			},
			createFigure : function() {
				return new Gef.gef.figures.GraphicalViewport(this)
			},
			getParentEl : function() {
				return this.getViewer().canvasEl
			},
			render : function() {
				this.figure.render();
				this.getContents().refresh()
			}
		});
Gef.ns("Gef.gef.support");
Gef.gef.support.PaletteHelper = Gef.extend(Object, {
			getSource : Gef.emptyFn,
			render : Gef.emptyFn,
			getPaletteConfig : Gef.emptyFn
		});
Gef.ns("Gef.gef.tracker");
Gef.gef.tracker.BrowserListener = Gef.extend(Object, {
	constructor : function($) {
		this.graphicalViewer = $;
		this.selectionManager = new Gef.gef.tracker.SelectionManager(this);
		this.enabled = true;
		this.dragging = false;
		this.activeTracker = null;
		this.initTrackers();
		this.initEvents()
	},
	initTrackers : function() {
		this.trackers = [];
		if (Gef.editable !== false) {
			this.trackers
					.push(new Gef.gef.tracker.KeyPressRequestTracker(this));
			this.trackers
					.push(new Gef.gef.tracker.DirectEditRequestTracker(this));
			this.trackers.push(new Gef.gef.tracker.ToolTracker(this));
			this.trackers
					.push(new Gef.gef.tracker.CreateNodeRequestTracker(this));
			this.trackers
					.push(new Gef.gef.tracker.CreateEdgeRequestTracker(this));
			this.trackers
					.push(new Gef.gef.tracker.ResizeNodeRequestTracker(this));
			this.trackers
					.push(new Gef.gef.tracker.ResizeEdgeRequestTracker(this));
			this.trackers
					.push(new Gef.gef.tracker.MoveEdgeRequestTracker(this));
			this.trackers
					.push(new Gef.gef.tracker.MoveNodeRequestTracker(this));
			this.trackers
					.push(new Gef.gef.tracker.MoveTextRequestTracker(this));
			this.trackers.push(new Gef.gef.tracker.MarqueeRequestTracker(this))
		}
		this.selectionRequestTracker = new Gef.gef.tracker.SelectionRequestTracker(this);
		this.selectionListenerTracker = new Gef.gef.tracker.SelectionListenerTracker(this)
	},
	initEvents : function() {
		this.initMouseDownEvent();  //取消鼠标点击
		this.initMouseMoveEvent();
		this.initMouseUpEvent();
		this.initDoubleClickEvent();
        this.initKeyDownEvent();
		this.initKeyUpEvent()
	},
	initMouseDownEvent : function() {
		var $ = this, _ = function(A) {
			var _ = Gef.isIE ? event : A;
			$.mouseDown(_)
		};
		if (Gef.isIE)
			document.attachEvent("onmousedown", _);
		else
			document.addEventListener("mousedown", _, false)
	},
	initMouseMoveEvent : function() {
		var $ = this, _ = function(A) {
			var _ = Gef.isIE ? event : A;
			$.mouseMove(_)
		};
		if (Gef.isIE)
			document.attachEvent("onmousemove", _);
		else
			document.addEventListener("mousemove", _, false)
	},
	initMouseUpEvent : function() {
		var $ = this, _ = function(A) {
			var _ = Gef.isIE ? event : A;
			$.mouseUp(_)
		};
		if (Gef.isIE)
			document.attachEvent("onmouseup", _);
		else
			document.addEventListener("mouseup", _, false)
	},
	initDoubleClickEvent : function() {
		var $ = this, _ = function(A) {
			var _ = Gef.isIE ? event : A;
			$.doubleClick(_)
		};
		if (Gef.isIE)
			document.attachEvent("ondblclick", _);
		else
			document.addEventListener("dblclick", _, false)
	},
	initKeyDownEvent : function() {
		var $ = this, _ = function(A) {
			var _ = Gef.isIE ? event : A;
			$.keyDown(_)
		};
		if (Gef.isIE)
			document.attachEvent("onkeydown", _);
		else
			document.addEventListener("keydown", _, false)
	},
	initKeyUpEvent : function() {
		var $ = this, _ = function(A) {
			var _ = Gef.isIE ? event : A;
			$.keyUp(_)
		};
		if (Gef.isIE)
			document.attachEvent("onkeyup", _);
		else
			document.addEventListener("keyup", _, false)
	},
	fireEvent : function(E, A) {
		if (this.enabled !== true)
			return;
		var _ = this.getXY(A), D = this.getTarget(A), B = {
			e : A,
			eventName : E,
			point : _,
			target : D
		};
		try {
			if (this.selectionRequestTracker.understandRequest(B))
				this.selectionRequestTracker.processRequest(B)
		} catch (C) {
			Gef.error(C, "select")
		}
		try {
			if (this.activeTracker == null)
				Gef.each(this.trackers, function($) {
							var _ = !$.understandRequest(B);
							return _
						}, this);
			if (this.activeTracker != null) {
				var $ = this.activeTracker.processRequest(B);
				if ($)
					this.stopEvent(A)
			}
		} catch (C) {
			Gef.error(C, "fireEvent")
		}
		try {
			if (this.selectionListenerTracker.understandRequest(B))
				this.selectionListenerTracker.processRequest(B)
		} catch (C) {
			Gef.error(C, "selectlistener")
		}
	},
	mouseDown : function($) {
		this.fireEvent("MOUSE_DOWN", $)
	},
	mouseMove : function($) {
		this.fireEvent("MOUSE_MOVE", $)
	},
	mouseUp : function($) {
		this.fireEvent("MOUSE_UP", $)
	},
	doubleClick : function($) {
		this.fireEvent("DBL_CLICK", $)
	},
	keyDown : function($) {
		this.fireEvent("KEY_DOWN", $)
	},
	keyUp : function($) {
		this.fireEvent("KEY_UP", $)
	},
	getXY : function($) {
		var _ = {};
		if (typeof window.pageYOffset != "undefined") {
			_.x = window.pageXOffset;
			_.y = window.pageYOffset
		} else if (typeof document.compatMode != "undefined"
				&& document.compatMode != "BackCompat") {
			_.x = document.documentElement.scrollLeft;
			_.y = document.documentElement.scrollTop
		} else if (typeof document.body != "undefined") {
			_.x = document.body.scrollLeft;
			_.y = document.body.scrollTop
		}
		var C = this.graphicalViewer.getCanvasLocation(), B = $.clientX + _.x, A = $.clientY
				+ _.y;
		return {
			x : B - C.x,
			y : A - C.y,
			absoluteX : B,
			absoluteY : A
		}
	},
	getTarget : function($) {
		return Gef.isIE ? $.srcElement : $.target
	},
	stopEvent : function($) {
		if (Gef.isIE)
			$.returnValue = false;
		else
			$.preventDefault()
	},
	getViewer : function() {
		return this.graphicalViewer
	},
	getSelectionManager : function() {
		return this.selectionManager
	},
	disable : function() {
		this.enabled = false
	},
	enable : function() {
		this.enabled = true
	}
});
Gef.ns("Gef.gef.tracker");
Gef.gef.tracker.RequestTracker = Gef.extend(Object, {
			understandRequest : Gef.emptyFn,
			processRequest : Gef.emptyFn
		});
Gef.ns("Gef.gef.tracker");
Gef.gef.tracker.AbstractRequestTracker = Gef.extend(
		Gef.gef.tracker.RequestTracker, {
			constructor : function($) {
				this.browserListener = $;
				this.reset()
			},
			reset : function() {
				this.status = "NONE";
				this.temp = {};
				this.browserListener.activeTracker = null
			},
			getDraggingRect : function() {
				if (!this.draggingRect) {
					this.draggingRect = new Gef.figure.DraggingRectFigure({
								x : -90,
								y : -90,
								w : 48,
								h : 48
							});
					this.getDraggingLayer().addChild(this.draggingRect);
					this.draggingRect.render()
				}
				return this.draggingRect
			},
			createDraggingRects : function() {
				if (!this.draggingRects)
					this.draggingRects = [];
				var $ = new Gef.figure.DraggingRectFigure({
							x : -90,
							y : -90,
							w : 48,
							h : 48
						});
				this.getDraggingLayer().addChild($);
				$.render();
				this.draggingRects.push($);
				return $
			},
			getDraggingRects : function($) {
				return this.draggingRects[$]
			},
			removeDraggingRects : function($) {
				if (!this.draggingRects)
					this.draggingRects = [];
				Gef.each(this.draggingRects, function($) {
							$.remove()
						}, this);
				this.draggingRects = []
			},
			getDraggingEdge : function() {
				if (!this.draggingEdge) {
					this.draggingEdge = new Gef.figure.DraggingEdgeFigure({
								x1 : -1,
								y1 : -1,
								x2 : -1,
								y2 : -1
							});
					this.getDraggingLayer().addChild(this.draggingEdge);
					this.draggingEdge.render()
				}
				return this.draggingEdge
			},
			isInPalette : function($) {
				return this
						.isIn($, this.getViewer().getPaletteLocation(), true)
			},
			isInCanvas : function($) {
				return this.isIn($, this.getViewer().getCanvasLocation(), true)
			},
			isIn : function(_, A, $) {
				if ($ === true)
					return _.absoluteX > A.x && _.absoluteX < A.x + A.w
							&& _.absoluteY > A.y && _.absoluteY < A.y + A.h;
				else
					return _.x > A.x && _.x < A.x + A.w && _.y > A.y
							&& _.y < A.y + A.h
			},
			getPaletteConfig : function($) {
				return this.getViewer().getPaletteConfig($.point, $.target)
			},
			findEditPartAt : function(H) {
				var I = H.point, B = null, _ = this.browserListener
						.getSelectionManager().getDefaultSelected();
				if (_) {
					var J = this.browserListener.getSelectionManager()
							.findNodeHandle(_);
					if (J && J.getDirectionByPoint)
						if (J.getDirectionByPoint(I))
							return _
				}
				Gef.each(this.getConnectionLayer().getChildren(), function(_) {
					for (var F = 0, E = _.points.length - 1; F < E; F++) {
						var C = _.points[F], A = _.points[F + 1], D = new Geom.Line(
								C[0], C[1], A[0], A[1]), $ = D
								.getPerpendicularDistance(I.x, I.y);
						if ($ < 8) {
							B = this.getEditPartByFigure(_);
							return false
						}
					}
				}, this);
				if (B)
					return B;
				var A = this.getNodeLayer().getChildren();
				for (var C = A.length - 1; C >= 0; C--) {
					var E = A[C], D = H.target.getAttribute("id");
					if (this.isIn(I, E) && D != null
							&& D.indexOf("_Gef_") != -1) {
						B = this.getEditPartByFigure(E);
						return B
					}
				}
				B = this.getContents();
				var F = H.target, G = F.getAttribute("edgeId");
				if (G != null)
					if (F.tagName == "text" || F.tagName == "textbox") {
						var $ = null, $ = this.getConnectionByConnectionId(G);
						if ($ != null)
							B = $.editPart
					}
				return B
			},
			getViewer : function() {
				return this.browserListener.getViewer()
			},
			getEditor : function() {
				return this.getViewer().getEditor()
			},
			getContents : function() {
				return this.getViewer().getContents()
			},
			getModelFactory : function() {
				return this.getEditor().getModelFactory()
			},
			getCommandStack : function() {
				return this.getViewer().getEditDomain().getCommandStack()
			},
			executeCommand : function(A, $) {
				var _ = A.getCommand($);
				if (_ != null)
					this.getCommandStack().execute(_)
			},
			getDraggingLayer : function() {
				return this.getViewer().getLayer("LAYER_DRAGGING")
			},
			getNodeLayer : function() {
				return this.getViewer().getLayer("LAYER_NODE")
			},
			getConnectionLayer : function() {
				return this.getViewer().getLayer("LAYER_CONNECTION")
			},
			getHandleLayer : function() {
				return this.getViewer().getLayer("LAYER_HANDLE")
			},
			getTargetEditPart : function() {
				return this.getContents()
			},
			getEditPartByFigure : function($) {
				return $.editPart
			},
			isConnection : function() {
				return this.getViewer().getActivePalette() != null
						&& this.getViewer().getActivePalette().isConnection === true
			},
			notConnection : function() {
				return !this.isConnection()
			},
			getSelectionManager : function() {
				return this.browserListener.getSelectionManager()
			},
			getSelectedNodes : function() {
				return this.getSelectionManager().getSelectedNodes()
			},
			hasSelectedNoneOrOne : function() {
				return this.getSelectionManager().getSelectedCount() < 2
			},
			isMultiSelect : function($) {
				return $.e.ctrlKey === true
			},
			notMultiSelect : function($) {
				return !this.isMultiSelect($)
			},
			getConnectionByConnectionId : function(_) {
				var $ = null;
				Gef.each(this.getConnectionLayer().getChildren(), function(A) {
							if (_ == A.el.id)
								$ = A
						}, this);
				return $
			},
			getNodeByNodeId : function(_) {
				var $ = null;
				Gef.each(this.getNodeLayer().getChildren(), function(A) {
							if (_ == A.el.id)
								$ = A
						}, this);
				return $
			}
		});
Gef.ns("Gef.gef.tracker");
Gef.gef.tracker.SelectionManager = Gef.extend(Object, {
			constructor : function($) {
				this.items = [];
				this.handles = {};
				this.browserListener = $
			},
			addSelectedConnection : function($) {
				if (this.selectedConnection)
					this.removeSelectedConnection(this.selectedConnection);
				this.resizeEdgeHandle = new Gef.figure.ResizeEdgeHandle();
				this.resizeEdgeHandle.edge = $.getFigure();
				this.addHandle(this.resizeEdgeHandle);
				this.resizeEdgeHandle.render();
				this.selectedConnection = $
			},
			removeSelectedConnection : function($) {
				this.resizeEdgeHandle.remove();
				this.selectedConnection = null;
				this.resizeEdgeHandle = null
			},
			addSelectedNode : function(A, $) {
				if (this.items.length == 1 && this.items[0] == A)
					return false;
				if (!$)
					this.clearAll();
				var _ = this.items.indexOf(A) != -1;
				if (_) {
					if ($) {
						this.removeSelectedNode(A, $);
						return false
					}
				} else {
					this.items.push(A);
					this.createNodeHandle(A)
				}
				return true
			},
			removeSelectedNode : function(A, $) {
				var _ = this.items.indexOf(A) != -1;
				if (_) {
					this.items.remove(A);
					this.removeNodeHandle(A)
				}
			},
			clearAll : function() {
				Gef.each(this.items, function($) {
							this.removeNodeHandle($)
						}, this);
				this.items = [];
				if (this.selectedConnection != null)
					this.removeSelectedConnection(this.selectedEdge);
				this.hideDraggingText()
			},
			selectAll : function() {
				this.clearAll();
				Gef.each(this.getNodes(), function($) {
							this.addSelectedNode($.editPart, true)
						}, this)
			},
			selectIn : function($) {
				this.clearAll();
				Gef.each(this.getNodes(), function(A) {
					var b=$
							var _ = A, C = _.x + _.w / 2, B = _.y + _.h / 2;
							if (C > $.x && C < $.x + $.w && B > $.y
									&& B < $.y + $.h)
								this.addSelectedNode(A.editPart, true)
						}, this)
			},
			createNodeHandle : function(A) {
				var $ = A.getModel().getId(), _ = this.handles[$];
				//do
//				var kk=new Gef.figure.NodeFigure();
//				this.handles[$] =kk;
//				kk.incomes=A.getFigure().incomes;
//				kk.outputs=A.getFigure().outputs;
//				kk.renderSvg("ak47");
//				kk.onRenderSvg();
				if (!_) {
					_ = new Gef.figure.ResizeNodeHandle();
					this.handles[$] = _;
					_.node = A.getFigure();
					this.addHandle(_);
					_.render()
				}
				return _
			},
			findNodeHandle : function(A) {
				var $ = A.getModel().getId(), _ = this.handles[$];
				return _
			},
			removeNodeHandle : function(A) {
				var $ = A.getModel().getId(), _ = this.handles[$];
				if (_ != null) {
					_.remove();
					this.handles[$] = null;
					delete this.handles[$]
				}
				return _
			},
			refreshHandles : function() {
				for (var _ in this.handles) {
					var $ = this.handles[_];
					$.refresh()
				}
				if (this.resizeEdgeHandle)
					this.resizeEdgeHandle.refresh()
			},
			addHandle : function($) {
				var _ = this.browserListener.getViewer()
						.getLayer("LAYER_HANDLE");
				_.addChild($)
			},
			addDragging : function(_) {
				var $ = this.browserListener.getViewer()
						.getLayer("LAYER_DRAGGING");
				$.addChild(_)
			},
			getNodes : function() {
				var $ = this.browserListener.getViewer().getLayer("LAYER_NODE");
				return $.getChildren()
			},
			getSelectedNodes : function() {
				return this.items
			},
			getSelectedCount : function() {
				return this.items.length
			},
			getSelectedConnection : function() {
				return this.selectedConnection
			},
			getDefaultSelected : function() {
				return this.browserListener.getViewer().getContents()
			},
			getCurrentSelected : function() {
				if (this.selectedConnection)
					return [this.selectedConnection];
				else if (this.items.length > 0)
					return this.items;
				else
					return [this.getDefaultSelected()]
			},
			getDraggingText : function($) {
				if (!this.draggingText) {
					this.draggingText = new Gef.figure.DraggingTextFigure($);
					this.addDragging(this.draggingText);
					this.draggingText.render()
				}
				this.draggingText.edge = $;
				this.draggingText.show();
				return this.draggingText
			},
			hideDraggingText : function() {
				if (this.draggingText)
					this.draggingText.hide()
			}
		});
Gef.ns("Gef.gef.tracker");
Gef.gef.tracker.CreateNodeRequestTracker = Gef.extend(
		Gef.gef.tracker.AbstractRequestTracker, {
			DRAGGING_CREATE_NODE : "DRAGGING_CREATE_NODE",
			understandRequest : function($) {
				if (this.status != "NONE") {
					this.reset();
					return false
				}
				if ($.eventName != "MOUSE_DOWN" || !this.isInPalette($.point))
					return false;
				var _ = this.getPaletteConfig($);
				if (_ == null || _.creatable === false)
					return false;
				this.paletteConfig = _;
				this.status = this.DRAGGING_CREATE_NODE;
				this.browserListener.activeTracker = this;
				return true
			},
			processRequest : function($) {
				if (this.status == "NONE") {
					this.reset();
					return false
				}
				if ($.eventName == "MOUSE_DOWN")
					this.drag($);
				else if ($.eventName == "MOUSE_MOVE")
					this.move($);
				else if ($.eventName == "MOUSE_UP")
					this.drop($);
				return true
			},
			drag : function(_) {
				var A = this.paletteConfig, $ = A.w, C = A.h;
				if (isNaN($) || $ < 0)
					$ = 48;
				if (isNaN(C) || C < 0)
					C = 48;
				var D = $ * -1, B = C * -1;
				this.getDraggingRect().update(D, B, $, C)
			},
			move : function(A) {
				var $ = this.getDraggingRect(), _ = A.point, C = _.x - $.w / 2, B = _.y
						- $.h / 2;
				$.moveTo(C, B)
			},
			drop : function(_) {
				if (this.isInCanvas(_.point)) {
					var $ = this.getDraggingRect(), A = this.paletteConfig.text;
					_.role = {
						name : "CREATE_NODE",
						rect : {
							x : _.point.x - $.w / 2,
							y : _.point.y - $.h / 2,
							w : $.w,
							h : $.h
						},
						node : this.getModelFactory().createModel(A)
					};
					this.executeCommand(this.getTargetEditPart(), _)
				}
				this.reset()
			},
			reset : function() {
				Gef.gef.tracker.CreateNodeRequestTracker.superclass.reset
						.call(this);
				this.paletteConfig = null;
				if (this.browserListener.getViewer().rendered) {
					var $ = this.getDraggingRect(), A = $.w * -1, _ = $.h * -1;
					$.moveTo(A, _)
				}
			}
		});
Gef.ns("Gef.gef.tracker");
Gef.gef.tracker.CreateEdgeRequestTracker = Gef.extend(
		Gef.gef.tracker.AbstractRequestTracker, {
			DRAGGING_CREATE_EDGE : "DRAGGING_CREATE_EDGE",
			understandRequest : function($) {
				if (this.status != "NONE") {
					this.reset();
					return false
				}
				if (!this.isInCanvas($.point) || this.notConnection()
						|| $.eventName != "MOUSE_DOWN")
					return false;
				var _ = this.findEditPartAt($);
				if (_ == null || _.getClass() != "node" || !_.canCreateOutgo())
					return false;
				this.temp.editPart = _;
				this.status = this.DRAGGING_CREATE_EDGE;
				this.browserListener.activeTracker = this;
				return true
			},
			processRequest : function($) {
				if (this.status == "NONE") {
					this.reset();
					return false
				}
				if ($.eventName == "MOUSE_DOWN")
					this.drag($);
				else if ($.eventName == "MOUSE_MOVE")
					this.move($);
				else if ($.eventName == "MOUSE_UP")
					this.drop($);
				return true
			},
			drag : function($) {
				this.getDraggingEdge().update(-1, -1, -1, -1)
			},
			move : function(B) {
				var A = B.point, $ = this.temp.editPart.getFigure(), C = {
					x : $.x,
					y : $.y,
					w : $.w,
					h : $.h
				}, _ = this.getDraggingEdge();
				_.updateForDragging(C, A)
			},
			drop : function(A) {
				var _ = this.getDraggingEdge(), D = this.temp.editPart, B = this
						.findEditPartAt(A);
				if (D != B && B.getClass() == "node" && B.canCreateIncome(D)) {
					var $ = this.getViewer().getActivePalette().text, C = this
							.getModelFactory().createModel($);
					if (D.getOutgoingConnections().length > 0)
						C.text = "to " + B.getModel().text;
					else
						// C.text = ""; 
						C.text = "to " + B.getModel().text; // 
					A.role = {
						name : "CREATE_EDGE",
						rect : {
							x1 : _.x1,
							y1 : _.y1,
							x2 : _.x2,
							y2 : _.y2
						},
						source : D.getModel(),
						target : B.getModel(),
						model : C
					};
					this.executeCommand(this.temp.editPart, A)
				}
				this.reset()
			},
			reset : function() {
				Gef.gef.tracker.CreateEdgeRequestTracker.superclass.reset
						.call(this);
				if (this.browserListener.getViewer().rendered)
					this.getDraggingEdge().moveToHide()
			}
		});
Gef.ns("Gef.gef.tracker");
Gef.gef.tracker.MoveNodeRequestTracker = Gef.extend(
		Gef.gef.tracker.AbstractRequestTracker, {
			DRAGGING_MOVE_NODE : "DRAGGING_MOVE_NODE",
			understandRequest : function($) {
				if (this.status != "NONE") {
					this.reset();
					return false
				}
				if (!this.isInCanvas($.point) || this.isConnection())
					return false;
				if ($.eventName != "MOUSE_DOWN")
					return false;
				var _ = this.findEditPartAt($);
				if (_ == null || _.getClass() != "node")
					return false;
				this.temp = {
					x : $.point.x,
					y : $.point.y,
					editPart : _
				};
				this.status = this.DRAGGING_MOVE_NODE;
				this.browserListener.activeTracker = this;
				return true
			},
			processRequest : function($) {
				if (this.status == "NONE") {
					this.reset();
					return false
				}
				if ($.eventName == "MOUSE_DOWN")
					this.drag($);
				else if ($.eventName == "MOUSE_MOVE")
					this.move($);
				else if ($.eventName == "MOUSE_UP")
					this.drop($);
				return true
			},
			drag : function($) {
				Gef.each(this.getSelectedNodes(), function(B) {
							var A = B.getFigure(), _ = A.w, D = A.h, E = A.x
									+ $.point.x - this.temp.x, C = A.y
									+ $.point.y - this.temp.y;
							this.createDraggingRects().update(_ * -1, D * -1,
									_, D)
						}, this)
			},
			move : function($) {
				Gef.each(this.getSelectedNodes(), function(C, A) {
					var _ = this.getDraggingRects(A), B = C.getFigure(), E = B.x
							+ $.point.x - this.temp.x, D = B.y + $.point.y
							- this.temp.y;
					_.moveTo(E, D)
				}, this)
			},
			drop : function(A) {
				var $ = this.getDraggingRect(), _ = [];
				Gef.each(this.getSelectedNodes(), function($) {
							_.push($.getModel())
						});
				if (A.point.x != this.temp.x || A.point.y != this.temp.y) {
					A.role = {
						name : "MOVE_NODE",
						nodes : _,
						dx : A.point.x - this.temp.x,
						dy : A.point.y - this.temp.y
					};
					this.executeCommand(this.getContents(), A);
					this.getSelectionManager().refreshHandles()
				}
				this.reset()
			},
			reset : function() {
				Gef.gef.tracker.MoveNodeRequestTracker.superclass.reset
						.call(this);
				if (this.browserListener.getViewer().rendered)
					this.removeDraggingRects()
			}
		});
Gef.ns("Gef.gef.tracker");
Gef.gef.tracker.MoveEdgeRequestTracker = Gef.extend(
		Gef.gef.tracker.AbstractRequestTracker, {
			DRAGGING_MOVE_EDGE : "DRAGGING_MOVE_EDGE",
			understandRequest : function(C) {
				if (this.status != "NONE") {
					this.reset();
					return false
				}
				if (!this.isInCanvas(C.point))
					return false;
				if (C.eventName != "MOUSE_DOWN")
					return false;
				var D = C.target;
				if (!D.id.indexOf(":"))
					return false;
				var E = D.id.split(":"), A = E[0], B = E[1];
				if (B != "start" && B != "end")
					return false;
				var _ = this.getConnectionByConnectionId(A);
				if (_ == null)
					return false;
				var $ = this.getSelectionManager().resizeEdgeHandle;
				if ($ == null)
					return false;
				this.temp = {
					editPart : _.editPart,
					handle : $,
					direction : B
				};
				this.status = this.DRAGGING_MOVE_EDGE;
				this.browserListener.activeTracker = this;
				return true
			},
			processRequest : function($) {
				if (this.status == "NONE") {
					this.reset();
					return true
				}
				if ($.eventName == "MOUSE_DOWN")
					this.drag($);
				else if ($.eventName == "MOUSE_MOVE")
					this.move($);
				else if ($.eventName == "MOUSE_UP")
					this.drop($);
				return false
			},
			drag : function(C) {
				var B = C.point, _ = this.temp.direction, D = this.temp.editPart, $ = null, E = {};
				if (_ == "start")
					$ = D.getTarget().getFigure();
				else
					$ = D.getSource().getFigure();
				var E = {
					x : $.x,
					y : $.y,
					w : $.w,
					h : $.h
				}, A = this.getDraggingEdge();
				A.updateForMove(D.getFigure(), _, B)
			},
			move : function(C) {
				var B = C.point, _ = this.temp.direction, D = this.temp.editPart, $ = null, E = {};
				if (_ == "start")
					$ = D.target.figure;
				else
					$ = D.source.figure;
				var E = {
					x : $.x,
					y : $.y,
					w : $.w,
					h : $.h
				}, A = this.getDraggingEdge();
				A.updateForMove(D.getFigure(), _, B)
			},
			drop : function(D) {
				var C = this.getDraggingEdge(), H = this.findEditPartAt(D), A = this.temp.editPart;
				if (H.getClass() == "node") {
					var B = this.temp.direction;
					if ((B == "start" && H.canCreateOutgo(A.target))
							|| (B == "end" && H.canCreateIncome(A.source))) {
						var _ = null, F = null;
						if (B == "start") {
							_ = H.getModel();
							F = A.target.getModel()
						} else {
							_ = A.source.getModel();
							F = H.getModel()
						}
						var $ = new Gef.commands.CompoundCommand(), I = this.temp.editPart.model, G = I
								.getType(), E = this.getModelFactory()
								.createModel(G);
						$
								.addCommand(new Gef.gef.command.RemoveConnectionCommand(I));
						$
								.addCommand(new Gef.gef.command.CreateConnectionCommand(
										E, _, F));
						$
								.addCommand(new Gef.gef.command.ResizeConnectionCommand(
										E, [], I.innerPoints));
						this.getCommandStack().execute($);
						this.getSelectionManager()
								.addSelectedConnection(E.editPart)
					}
				}
				this.reset()
			},
			reset : function() {
				Gef.gef.tracker.MoveEdgeRequestTracker.superclass.reset
						.call(this);
				if (this.browserListener.getViewer().rendered) {
					this.getDraggingEdge().moveToHide();
					this.getSelectionManager().refreshHandles()
				}
			}
		});
Gef.ns("Gef.gef.tracker");
Gef.gef.tracker.MoveTextRequestTracker = Gef.extend(
		Gef.gef.tracker.AbstractRequestTracker, {
			DRAGGING_MOVE_TEXT : "DRAGGING_MOVE_TEXT",
			understandRequest : function(B) {
				if (this.status != "NONE") {
					this.reset();
					return false
				}
				if (!this.isInCanvas(B.point))
					return false;
				if (B.eventName != "MOUSE_DOWN")
					return false;
				var _ = B.target, A = _.getAttribute("edgeId");
				if (A == null)
					return false;
				if (_.tagName != "text" && _.tagName != "textbox")
					return false;
				var $ = null, $ = this.getConnectionByConnectionId(A);
				if ($ == null)
					return false;
				this.temp = {
					editPart : $.editPart,
					x : B.point.x,
					y : B.point.y
				};
				this.status = this.DRAGGING_MOVE_TEXT;
				this.browserListener.activeTracker = this;
				return true
			},
			processRequest : function($) {
				if (this.status == "NONE") {
					this.reset();
					return false
				}
				if ($.eventName == "MOUSE_DOWN")
					this.drag($);
				else if ($.eventName == "MOUSE_MOVE")
					this.move($);
				else if ($.eventName == "MOUSE_UP")
					this.drop($);
				return true
			},
			drag : function(_) {
				var $ = this.getDraggingText();
				$.refresh();
				this.temp.oldX = $.edge.textX;
				this.temp.oldY = $.edge.textY
			},
			move : function(B) {
				var A = this.getDraggingText(), $ = B.point.x - this.temp.x, _ = B.point.y
						- this.temp.y;
				A.edge.textX = this.temp.oldX + $;
				A.edge.textY = this.temp.oldY + _;
				A.refresh()
			},
			drop : function(A) {
				var C = this.temp.oldX, B = this.temp.oldY, _ = C + A.point.x
						- this.temp.x, $ = B + A.point.y - this.temp.y;
				A.role = {
					name : "MOVE_TEXT",
					oldTextX : C,
					oldTextY : B,
					newTextX : _,
					newTextY : $,
					edge : this.temp.editPart
				};
				this.executeCommand(this.temp.editPart, A);
				this.reset()
			},
			getDraggingText : function() {
				var $ = this.temp.editPart.getFigure();
				return this.getSelectionManager().getDraggingText($)
			},
			reset : function() {
				Gef.gef.tracker.MoveTextRequestTracker.superclass.reset
						.call(this)
			}
		});
Gef.ns("Gef.gef.tracker");
Gef.gef.tracker.ResizeNodeRequestTracker = Gef.extend(
		Gef.gef.tracker.AbstractRequestTracker, {
			DRAGGING_RESIZE_NODE : "DRAGGING_RESIZE_NODE",
			understandRequest : function(C) {
				if (this.status != "NONE") {
					this.reset();
					return false
				}
				if (!this.isInCanvas(C.point))
					return false;
				if (C.eventName != "MOUSE_DOWN")
					return false;
				var D = C.target;
				if (D.id.indexOf(":") == -1)
					return false;
				var F = D.id.split(":"), _ = F[0], B = F[1], $ = this
						.getNodeByNodeId(_);
				if ($ == null)
					return false;
				else if (!$.editPart.canResize())
					return false;
				var E = this.getSelectionManager().handles, A = E[$.editPart
						.getModel().getId()];
				if (A == null)
					return false;
				this.temp = {
					editPart : $.editPart,
					handle : A,
					direction : B
				};
				this.status = this.DRAGGING_RESIZE_NODE;
				this.browserListener.activeTracker = this;
				return true
			},
			processRequest : function($) {
				if (this.status == "NONE") {
					this.reset();
					return false
				}
				if ($.eventName == "MOUSE_DOWN")
					this.drag($);
				else if ($.eventName == "MOUSE_MOVE")
					this.move($);
				else if ($.eventName == "MOUSE_UP")
					this.drop($);
				return true
			},
			drag : function(_) {
				var A = this.temp.editPart.figure, $ = this.temp.direction;
				if ($ == "n") {
					this.temp.x = A.x + A.w / 2;
					this.temp.y = A.y
				} else if ($ == "s") {
					this.temp.x = A.x + A.w / 2;
					this.temp.y = A.y + A.h
				} else if ($ == "w") {
					this.temp.x = A.x;
					this.temp.y = A.y + A.h / 2
				} else if ($ == "e") {
					this.temp.x = A.x + A.w;
					this.temp.y = A.y + A.h / 2
				} else if ($ == "nw") {
					this.temp.x = A.x;
					this.temp.y = A.y
				} else if ($ == "ne") {
					this.temp.x = A.x + A.w;
					this.temp.y = A.y
				} else if ($ == "sw") {
					this.temp.x = A.x;
					this.temp.y = A.y + A.h
				} else if ($ == "se") {
					this.temp.x = A.x + A.w;
					this.temp.y = A.y + A.h
				}
				this.getDraggingRect().update(A.x, A.y, A.w, A.h)
			},
			move : function(G) {
				var H = G.point, F = this.temp.editPart.getFigure(), A = this.temp.direction, J = F.x, I = F.y, D = F.w, C = F.h, $ = H.x
						- this.temp.x, _ = H.y - this.temp.y;
				if (A == "n") {
					I = I + _;
					C = C - _
				} else if (A == "s")
					C = C + _;
				else if (A == "w") {
					J = J + $;
					D = D - $
				} else if (A == "e")
					D = D + $;
				else if (A == "nw") {
					J = J + $;
					D = D - $;
					I = I + _;
					C = C - _
				} else if (A == "ne") {
					D = D + $;
					I = I + _;
					C = C - _
				} else if (A == "sw") {
					J = J + $;
					D = D - $;
					C = C + _
				} else if (A == "se") {
					D = D + $;
					C = C + _
				}
				var B = {
					x : J,
					y : I,
					w : D,
					h : C
				};
				this.temp.rect = B;
				var E = this.getDraggingRect();
				E.update(B.x, B.y, B.w, B.h)
			},
			drop : function(A) {
				var _ = this.getDraggingRect(), B = this.temp.editPart, E = this.temp.rect.x, D = this.temp.rect.y, $ = this.temp.rect.w, C = this.temp.rect.h;
				if ($ < 0)
					$ = 5;
				if (C < 0)
					C = 5;
				A.role = {
					name : "RESIZE_NODE",
					rect : {
						x : E,
						y : D,
						w : $,
						h : C
					},
					node : B.getModel()
				};
				this.executeCommand(B, A);
				this.temp.handle.refresh();
				this.reset()
			},
			reset : function() {
				Gef.gef.tracker.ResizeNodeRequestTracker.superclass.reset
						.call(this);
				if (this.browserListener.getViewer().rendered)
					this.getDraggingRect().update(-1, -1, 1, 1)
			}
		});
Gef.ns("Gef.gef.tracker");
Gef.gef.tracker.ResizeEdgeRequestTracker = Gef.extend(
		Gef.gef.tracker.AbstractRequestTracker, {
			DRAGGING_RESIZE_EDGE : "DRAGGING_RESIZE_EDGE",
			understandRequest : function(J) {
				if (this.status != "NONE") {
					this.reset();
					return false
				}
				if (!this.isInCanvas(J.point))
					return false;
				if (J.eventName != "MOUSE_DOWN")
					return false;
				var K = J.target, F = K.id;
				if (F == null || F.indexOf(":middle:") == -1)
					return false;
				var I = F.substring(0, F.indexOf(":middle:")), _ = this
						.getConnectionByConnectionId(I);
				if (_ == null)
					return false;
				var $ = F.substring(F.indexOf(":middle:") + ":middle:".length)
						.split(","), C = parseInt($[0], 10), G = parseInt($[1],
						10), D = this.getSelectionManager().resizeEdgeHandle, A = [];
				Gef.each(_.innerPoints, function($) {
							A.push([$[0], $[1]])
						});
				var H = null, E = null, B = null;
				if (C == G) {
					H = _.innerPoints[C];
					if (C == 0)
						E = [_.x1, _.y1];
					else
						E = _.innerPoints[C - 1];
					if (G == _.innerPoints.length - 1)
						B = [_.x2, _.y2];
					else
						B = _.innerPoints[C + 1]
				} else {
					if (C == -1)
						E = [_.x1, _.y1];
					else
						E = _.innerPoints[C];
					if (G >= _.innerPoints.length)
						B = [_.x2, _.y2];
					else
						B = _.innerPoints[G];
					H = [(E[0] + B[0]) / 2, (E[1] + B[1]) / 2];
					_.innerPoints.splice(C + 1, 0, H);
					D.modify()
				}
				this.temp = {
					editPart : _.editPart,
					point : H,
					x : H[0],
					y : H[1],
					oldX : J.point.x,
					oldY : J.point.y,
					prevIndex : C,
					nextIndex : G,
					prev : E,
					next : B,
					oldInnerPoints : A
				};
				this.status = this.DRAGGING_RESIZE_EDGE;
				this.browserListener.activeTracker = this;
				return true
			},
			processRequest : function($) {
				if (this.status == "NONE") {
					this.reset();
					return false
				}
				if ($.eventName == "MOUSE_DOWN")
					this.drag($);
				else if ($.eventName == "MOUSE_MOVE")
					this.move($);
				else if ($.eventName == "MOUSE_UP")
					this.drop($);
				return true
			},
			drag : function($) {
				this.getSelectionManager().hideDraggingText()
			},
			move : function(A) {
				var $ = A.point.x - this.temp.oldX, _ = A.point.y
						- this.temp.oldY;
				this.temp.point[0] = this.temp.x + $;
				this.temp.point[1] = this.temp.y + _;
				var B = this.getSelectionManager().resizeEdgeHandle;
				if (B)
					B.modify();
				else
					this.reset()
			},
			drop : function($) {
				var _ = this.temp.editPart;
				if (this
						.isSameLine($.point.x, $.point.y, this.temp.prev[0],
								this.temp.prev[1], this.temp.next[0],
								this.temp.next[1]))
					_.getFigure().innerPoints.splice(this.temp.nextIndex, 1);
				$.role = {
					name : "RESIZE_EDGE",
					rect : {
						x : _.figure.x,
						y : _.figure.y,
						w : _.figure.w,
						h : _.figure.h
					},
					oldInnerPoints : this.temp.oldInnerPoints,
					newInnerPoints : _.getFigure().innerPoints
				};
				this.executeCommand(_, $);
				this.reset()
			},
			isSameLine : function(E, _, F, A, C, B) {
				var J = F - E, I = A - _, K = C - E, H = B - _, L = J * K + I
						* H, G = Math.sqrt((J * J + I * I) * (K * K + H * H)), $ = L
						/ G, D = Math.acos($) * 180 / Math.PI;
				return D > 170
			},
			reset : function() {
				Gef.gef.tracker.ResizeEdgeRequestTracker.superclass.reset
						.call(this);
				if (this.browserListener.getViewer().rendered)
					this.getSelectionManager().refreshHandles()
			}
		});
Gef.ns("Gef.gef.tracker");
Gef.gef.tracker.MarqueeRequestTracker = Gef.extend(
		Gef.gef.tracker.AbstractRequestTracker, {
			DRAGGING_MARQUEE : "DRAGGING_MARQUEE",
			understandRequest : function(A) {
				if (this.status != "NONE") {
					this.reset();
					return false
				}
				if (!this.isInCanvas(A.point))
					return false;
				if (A.eventName != "MOUSE_DOWN")
					return false;
				var B = this.findEditPartAt(A);
				if (B != this.getContents())
					return false;
				var _ = A.target;
				if (Gef.isVml && _.tagName == "DIV") {
					if (_.firstChild && _.firstChild.tagName == "DIV") {
						var $ = _.firstChild.getAttribute("id");
						if ($ != null && $.indexOf("_Gef_") != -1) {
							this.status = this.DRAGGING_MARQUEE;
							this.browserListener.activeTracker = this;
							return true
						}
					}
				} else if (Gef.isSvg && _.tagName == "svg") {
					this.status = this.DRAGGING_MARQUEE;
					this.browserListener.activeTracker = this;
					return true
				} else if (Gef.isSvg && _.tagName == "DIV" && _.firstChild
						&& _.firstChild.tagName == "svg") {
					this.status = this.DRAGGING_MARQUEE;
					this.browserListener.activeTracker = this;
					return true
				}
				return false
			},
			processRequest : function($) {
				if (this.status == "NONE") {
					this.reset();
					return true
				}
				if ($.eventName == "MOUSE_DOWN")
					this.drag($);
				else if ($.eventName == "MOUSE_MOVE")
					this.move($);
				else if ($.eventName == "MOUSE_UP")
					this.drop($);
				return false
			},
			drag : function(_) {
				var $ = _.point;
				this.getDraggingRect().update($.x, $.y, 0, 0)
			},
			move : function(A) {
				var $ = this.getDraggingRect(), _ = A.point;
				$.update($.x, $.y, _.x - $.x, _.y - $.y)
			},
			drop : function(_) {
				var A = this.getDraggingRect(), $ = {
					x : _.point.x < A.x ? _.point.x : A.x,
					y : _.point.y < A.y ? _.point.y : A.y,
					w : A.w,
					h : A.h
				};
				this.getSelectionManager().selectIn($);
				this.reset()
			},
			reset : function() {
				Gef.gef.tracker.MarqueeRequestTracker.superclass.reset
						.call(this);
				if (this.browserListener.getViewer().rendered)
					this.getDraggingRect().update(-90, -90, 90, 50)
			}
		});
Gef.ns("Gef.gef.tracker");
Gef.gef.tracker.DirectEditRequestTracker = Gef.extend(
		Gef.gef.tracker.AbstractRequestTracker, {
			understandRequest : function($) {
				if (this.status != "NONE")
					this.reset();
				if (!this.isInCanvas($.point) || $.eventName != "DBL_CLICK")
					return false;
				if ($.target.tagName != "text" && $.target.tagName != "textbox")
					return false;
				this.status = "EDIT_START";
				this.browserListener.activeTracker = this;
				return true
			},
			processRequest : function(_) {
				if (this.status == "NONE") {
					this.reset();
					return false
				}
				if (_.eventName == "KEY_DOWN") {
					var $ = _.e.keyCode;
					if ($ == 10 || $ == 13)
						this.status = "EDIT_COMPLETE";
					if ($ == 27)
						this.status = "EDIT_CANCEL"
				}
				if (_.eventName == "MOUSE_DOWN" && _.target.tagName != "INPUT")
					if (this.status == "ALREADY_START_EDIT") {
						_.editType = "EDIT_COMPLETE";
						this.status = "EDIT_COMPLETE"
					}
				if (this.status == "EDIT_START")
					this.startEdit(_);
				else if (this.status == "EDIT_COMPLETE")
					this.completeEdit(_);
				else if (this.status == "EDIT_CANCEL")
					this.cancelEdit(_);
				return false
			},
			startEdit : function(A) {
				var B = this.findEditPartAt(A);
				if (B.getClass() == "node") {
					if (B.getFigure().updateAndShowText != null) {
						//baseCode
						/*this.getTextEditor().showForNode(B.getFigure());
						this.temp.editPart = B;
						this.status = "ALREADY_START_EDIT"*/
						//hey 禁止使用节点双击编辑name
						this.status = "NONE"
					} else
						this.status = "NONE"
				} else if (this.isText(A.target)) {
					//baseCode
					/*var _ = A.target.getAttribute("edgeId"), $ = this
							.getConnectionByConnectionId(_);
					this.getTextEditor().showForEdge($);
					this.temp.editPart = $.editPart;
					this.status = "ALREADY_START_EDIT"*/
					//hey 禁止使用线条双击编辑name
					this.status = "NONE"
				}
			},
			completeEdit : function(A) {
				if (!this.temp.editPart)
					return;
				var B = this.temp.editPart, $ = this.getTextEditor().getValue();
				if ($ != B.getModel().name) {
					A.role = {
						name : "EDIT_TEXT",
						text : $
					};
					this.executeCommand(B, A)
				}
				var _ = this.getSelectionManager().draggingText;
				if (_)
					_.refresh();
				this.reset()
			},
			cancelEdit : function($) {
				this.reset()
			},
			isText : function($) {
				return (Gef.isVml && $.tagName == "textbox")
						|| (Gef.isSvg && $.tagName == "text")
			},
			getTextEditor : function() {
				if (!this.textEditor) {
					var A = this.browserListener.getViewer()
							.getCanvasLocation(), _ = A.x, $ = A.y;
					this.textEditor = new Gef.figure.TextEditor(_, $)
				}
				A = this.browserListener.getViewer().getCanvasLocation();
				this.textEditor.baseX = A.x;
				this.textEditor.baseY = A.y;
				this.textEditor.show();
				return this.textEditor
			},
			reset : function() {
				Gef.gef.tracker.DirectEditRequestTracker.superclass.reset
						.call(this);
				if (this.browserListener.getViewer().rendered)
					this.getTextEditor().hide()
			}
		});
Gef.ns("Gef.gef.tracker");
Gef.gef.tracker.SelectionRequestTracker = Gef.extend(
		Gef.gef.tracker.AbstractRequestTracker, {
			understandRequest : function(_) {
				if (this.status != "NONE")
					this.reset();
				var $ = _.eventName == "MOUSE_DOWN" || _.eventName == "KEY_UP";
				if ($)
					this.status = "SELECT";
				return $
			},
			processRequest : function(B) {
				if (this.status == "NONE") {
					this.reset();
					return false
				}
				if (B.eventName != "MOUSE_DOWN" && B.eventName != "KEY_UP") {
					this.reset();
					return false
				}
				var C = this.findEditPartAt(B);
				if (this.notMultiSelect(B)) {
					var A = this.getSelectedNodes();
					if (A.length > 1 && A.indexOf(C) != -1)
						return false
				}
				if (C.getClass() == "process")
					;
				else if (C.getClass() == "node") {
					var _ = this.addSelected(C, this.isMultiSelect(B));
					if (_) {
						var $ = this.createNodeHandle(C);
						$.refresh()
					}
				} else if (C.getClass() == "connection") {
					this.clearAll();
					this.addSelectedEdge(C)
				}
				return false
			},
			addSelectedEdge : function($) {
				this.getSelectionManager().addSelectedConnection($)
			},
			removeSelectedEdge : function($) {
				this.getSelectionManager().removeSelectedConnection($)
			},
			addSelected : function(_, $) {
				return this.getSelectionManager().addSelectedNode(_, $)
			},
			removeSelected : function(_, $) {
				this.getSelectionManager().removeSelectedNode(_, $)
			},
			clearAll : function() {
				this.getSelectionManager().clearAll()
			},
			selectAll : function() {
				this.getSelectionManager().selectAll()
			},
			selectIn : function($) {
				this.getSelectionManager().selectIn($)
			},
			createNodeHandle : function($) {
				return this.getSelectionManager().createNodeHandle($)
			},
			removeNodeHandle : function($) {
				return this.getSelectionManager.removeNodeHandle($)
			},
			refreshHandles : function() {
				this.getSelectionManager.refreshHandles()
			},
			reset : function() {
				this.status = "NONE"
			}
		});
Gef.ns("Gef.gef.tracker");
Gef.gef.tracker.SelectionListener = Gef.extend(Object, {
			selectionChanged : Gef.emptyFn
		});
Gef.ns("Gef.gef.tracker");
Gef.gef.tracker.SelectionListenerTracker = Gef.extend(
		Gef.gef.tracker.AbstractRequestTracker, {
			understandRequest : function($) {
				return $.eventName == "MOUSE_UP" || $.eventName == "KEY_DOWN"
			},
			processRequest : function(B) {
				var $ = this.getSelectionManager();
				if (!this.previousSelected)
					this.previousSelected = [$.getDefaultSelected()];
				var A = $.getCurrentSelected(), _ = $.getDefaultSelected(), C = false;
				if (this.previousSelected.length == A.length) {
					for (var D = 0; D < A.length; D++)
						if (A[D] != this.previousSelected[D]) {
							C = true;
							break
						}
				} else
					C = true;
				if (C === true) {
					Gef.each(this.getSelectionListeners(), function($) {
								$.selectionChanged(A, this.previousSelected, _)
							}, this);
					this.previousSelected = A
				}
				return false
			},
			getSelectionListeners : function() {
				if (!this.selectionListeners)
					this.selectionListeners = [];
				return this.selectionListeners
			},
			addSelectionListener : function($) {
				this.getSelectionListeners().push($)
			}
		});
Gef.ns("Gef.gef.tracker");
Gef.gef.tracker.DefaultSelectionListener = Gef.extend(
		Gef.gef.tracker.SelectionListener, {
			selectionChanged : function(A, $, _) {
				if (A.length == 1) {
					var B = A[0];
					if (B == _)
						this.selectDefault(_);
					else if (B.getClass() == "node")
						this.selectNode(B);
					else
						this.selectConnection(B)
				} else
					this.selectDefault(_)
			},
			selectNode : Gef.emptyFn,
			selectConnection : Gef.emptyFn,
			selectDefault : Gef.emptyFn
		});
Gef.ns("Gef.gef.tracker");
Gef.gef.tracker.ToolTracker = Gef.extend(
		Gef.gef.tracker.AbstractRequestTracker, {
			isTool : function(_) {
				var $ = false, A = null;
				//hey 为线条tool添加点击事件
				if(this.getSelectedNodes().length==0){
					if(this.getSelectionManager().selectedConnection&&
					   this.getSelectionManager().selectedConnection.figure&&
					   this.getSelectionManager().selectedConnection.figure.pointTools){
							Gef.each(this.getSelectionManager().selectedConnection.figure.getTools(), function(B) {
										if (B.isClicked(_)) {
											$ = true;
											A = B;
											return false
										}
										if ($ === true)
											return false
									})
					   }
				}else{
					Gef.each(this.getSelectedNodes(), function(B) {
							Gef.each(B.getFigure().getTools(), function(B) {
										if (B.isClicked(_)) {
											$ = true;
											A = B;
											return false
										}
										if ($ === true)
											return false
									})
						});
				}
				if ($ === true)
					this.selectedTool = A;
				return $
			},
			understandRequest : function($) {
				if (this.status != "NONE") {
					this.reset();
					return false
				}
				if ($.editType != null || $.draggingType != null)
					return false;
				if ($.eventName != "MOUSE_DOWN")
					return false;
				if (!this.isTool($))
					return false;
				var _ = this.getSelectedNodes()[0];
				if (this.selectedTool.needCheckOutgo() && !_.canCreateOutgo())
					return false;
				this.status = "TOOL_SELECTED";
				this.browserListener.activeTracker = this;
				return true
			},
			processRequest : function($) {
				if (this.status == "NONE") {
					this.reset();
					return false
				}
				if ($.eventName == "MOUSE_DOWN")
					this.drag($);
				else if ($.eventName == "MOUSE_MOVE")
					this.move($);
				else if ($.eventName == "MOUSE_UP")
					this.drop($);
				return true
			},
			drag : function($) {
				this.selectedTool.drag(this, $)
			},
			move : function($) {
				this.selectedTool.move(this, $)
			},
			drop : function($) {
				this.selectedTool.drop(this, $);
				this.reset()
			}
		});
Gef.ns("Gef.gef.tracker");
Gef.gef.tracker.KeyPressRequestTracker = Gef.extend(
		Gef.gef.tracker.AbstractRequestTracker, {
			KEY_PRESS : "KEY_PRESS",
			understandRequest : function(_) {
				if (this.status != "NONE") {
					this.reset();
					return false
				}
				if (_.target.tagName == "INPUT"
						|| _.target.tagName == "TEXTAREA")
					return false;
				if (_.eventName != "KEY_DOWN")
					return false;
				try {
					this.temp = {
						x : 0,
						y : 0
					};
					this.status = this.KEY_PRESS;
					this.browserListener.activeTracker = this;
					return true
				} catch ($) {
					Gef.error($, "key press");
					return false
				}
			},
			processRequest : function(_) {
				if (this.status == "NONE") {
					this.reset();
					return false
				}
				if (_.eventName == "KEY_DOWN") {		
					var $ = _.e.keyCode;
					if($==27||$==18||$==9){//键盘操作bug
						this.reset();
						return false
					}
					if ($ == 37 || $ == 38 || $ == 39 || $ == 40)
						this.move(_);
					if ($ == 46) {
						this.status = "REMOVE";
						this.removeAll(_);
						this.reset()
					}
					if (_.e.ctrlKey && $ == 65) {
						this.status = "SELECT_ALL";
						this.selectAllNodes(_);
						this.reset()
					}
				} else if (_.eventName == "KEY_UP") {
					$ = _.e.keyCode;
					if ($ == 37 || $ == 38 || $ == 39 || $ == 40)
						this.drop(_)
				}
				return true
			},
			move : function(A) {
				var $ = 0, _ = 0;
				switch (A.e.keyCode) {
					case 38 :
						_ = -1;
						break;
					case 40 :
						_ = 1;
						break;
					case 37 :
						$ = -1;
						break;
					case 39 :
						$ = 1;
						break
				}
				this.temp.x += $;
				this.temp.y += _;
				Gef.each(this.getSelectedNodes(), function(D, A) {
							var C = D.getFigure();
							try {
								var F = C.x + $, E = C.y + _;
								C.moveTo(F, E)
							} catch (B) {
								Gef.error(B, "move key press")
							}
						}, this);
				this.getSelectionManager().refreshHandles()
			},
			drop : function(_) {
				var $ = [];
				Gef.each(this.getSelectedNodes(), function(A) {
							var _ = A.getModel();
							$.push(_)
						});
				if (this.temp.x != 0 || this.temp.y != 0) {
					_.role = {
						name : "MOVE_NODE",
						nodes : $,
						dx : this.temp.x,
						dy : this.temp.y
					};
					this.executeCommand(this.getContents(), _)
				}
				this.reset()
			},
			reset : function() {
				Gef.gef.tracker.KeyPressRequestTracker.superclass.reset
						.call(this);
				if (this.browserListener.getViewer().rendered)
					this.removeDraggingRects()
			},
			removeAll : function(B) {
				try {
					var $ = this.getSelectionManager(), _ = $.selectedConnection, A = $.items;
					if (_ != null) {
						B.role = {
							name : "REMOVE_EDGE"
						};
						this.executeCommand(_, B);
						$.removeSelectedConnection()
					} else if (A.length > 0) {
						B.role = {
							name : "REMOVE_NODES",
							nodes : A
						};
						this.executeCommand(
								this.browserListener.graphicalViewer
										.getContents(), B);
						$.clearAll()
					}
				} catch (C) {
					Gef.error(C, "removeAll")
				}
			},
			selectAllNodes : function($) {
				this.getSelectionManager().selectAll()
			}
		});
Gef.ns("Gef.tool");
Gef.tool.AbstractTool = Gef.extend(Object, {
			constructor : function($) {
				Gef.apply(this, $ ? $ : {})
			},
			needCheckOutgo : function() {
				return true
			},
			getKey : function() {
				return "abstractTool"
			},
			getId : function($) {
				if ($) {
					this.node = $;
					this.id = $.getId() + ":" + this.getKey()
				}
				return this.id
			},
			render : function(_, $) {
				if (Gef.isVml)
					this.renderVml(_, $);
				else
					this.renderSvg(_, $)
			},
			renderVml : Gef.emptyFn,
			renderSvg : Gef.emptyFn,
			resize : function(B, A, $, _) {
				if (Gef.isVml)
					this.resizeVml(B, A, $, _);
				else
					this.resizeSvg(B, A, $, _)
			},
			resizeVml : Gef.emptyFn,
			resizeSvg : Gef.emptyFn,
			isClicked : function($) {
				if (Gef.isVml)
					return this.isClickedVml($);
				else
					return this.isClickedSvg($)
			},
			isClickedVml : Gef.emptyFn,
			isClickedSvg : Gef.emptyFn,
			drag : Gef.emptyFn,
			move : Gef.emptyFn,
			drop : Gef.emptyFn
		});
Gef.ns("Gef.tool");
Gef.tool.AbstractImageTool = Gef.extend(Gef.tool.AbstractTool, {
			getKey : function() {
				return "abstractImageTool"
			},
			getUrl : function() {
				return Gef.IMAGE_ROOT + "../../tools/new_event.png"
			},
			getNodeConfig : function() {
				return {
					text : "node",
					w : 48,
					h : 48
				}
			},
			getX : function($) {
				return $ + 5
			},
			getY : function($) {
				return 15
			},
			renderVml : function(A, $) {
				var _ = document.createElement("img");
				_.setAttribute("id", this.getId($));
				_.setAttribute("unselectable", "on");
				_.style.position = "absolute";
				_.style.left = this.getX($.w) + "px";
				_.style.top = this.getY($.h) + "px";
				_.style.width = "16px";
				_.style.height = "16px";
				_.setAttribute("opacity", "0.5");
				_.src = this.getUrl();
				A.appendChild(_);
				this.el = _;
				_.onmouseover = function() {
					_.setAttribute("opacity", "1.0")
				};
				_.onmouseout = function() {
					_.setAttribute("opacity", "0.5")
				}
			},
			renderSvg : function(A, $) {
				var _ = document.createElementNS(Gef.svgns, "image");
				_.setAttribute("id", this.getId($));
				//hey 渲染线条tools
				if($.pointTools){
					_.setAttribute("x", $.textEl.x.baseVal[0].value-15);
					_.setAttribute("y", $.textEl.y.baseVal[0].value-25);
				}else{
					_.setAttribute("x", this.getX($.w));
					_.setAttribute("y", this.getY($.h));
				}
				_.setAttribute("width", 16);
				_.setAttribute("height", 16);
				_.setAttributeNS(Gef.linkns, "xlink:href", this.getUrl());
				_.setAttribute("opacity", "0.5");
				A.appendChild(_);
				this.el = _;
				_.addEventListener("mouseover", function() {
							_.setAttribute("opacity", "1.0")
						}, false);
				_.addEventListener("mouseout", function() {
							_.setAttribute("opacity", "0.5")
						}, false)
			},
			resizeVml : function(B, A, $, _) {
				this.el.style.left = this.getX($) + "px";
				this.el.style.top = this.getY(_) + "px"
			},
			resizeSvg : function(B, A, $, _) {
				this.el.setAttribute("x", this.getX($));
				this.el.setAttribute("y", this.getY(_))
			},
			isClickedVml : function(A) {
				var _ = A.target, $ = _.getAttribute("id");
				if (!$)
					return false;
				if (_.tagName == "IMG" && $ == this.getId())
					return true
			},
			isClickedSvg : function(A) {
				var _ = A.target, $ = _.getAttribute("id");
				if (!$)
					return false;
				if (_.tagName == "image" && $ == this.getId())
					return true
			},
			drag : function(_) {
				var A = this.getNodeConfig();
				_.getDraggingRect().name = A.text;
				var $ = A.w, C = A.h;
				if (isNaN($) || $ < 0)
					$ = 48;
				if (isNaN(C) || C < 0)
					C = 48;
				var D = $ * -1, B = C * -1;
				_.getDraggingRect().update(D, B, $, C)
			},
			move : function(_, B) {
				var $ = _.getDraggingRect(), A = B.point, D = A.x - $.w / 2, C = A.y
						- $.h / 2;
				$.moveTo(D, C)
			},
			drop : function(A, B) {
				var $ = A.getDraggingRect();
				if (A.isInCanvas(B.point)) {
					var D = $.name;
					B.role = {
						name : "CREATE_NODE",
						rect : {
							x : B.point.x - $.w / 2,
							y : B.point.y - $.h / 2,
							w : $.w,
							h : $.h
						},
						node : A.getModelFactory().createModel(D)
					};
					var C = new Gef.commands.CompoundCommand();
					C.addCommand(new Gef.gef.command.CreateNodeCommand(
							B.role.node, A.getContents().getModel(),
							B.role.rect));
					var _ = this.node.editPart.getModel(), G = B.role.node, E = A
							.getModelFactory().createModel(this
									.getConnectionModelName());
					if (_.getOutgoingConnections().length > 0)
						E.text = "to " + G.text;
					else
						E.text = "";
					C.addCommand(new Gef.gef.command.CreateConnectionCommand(E,
							_, G));
					A.getCommandStack().execute(C)
				}
				var H = $.w * -1, F = $.h * -1;
				$.moveTo(H, F)
			}
		});
Gef.ns("Gef.tool");
Gef.tool.AbstractEdgeTool = Gef.extend(Gef.tool.AbstractImageTool, {
			getKey : function() {
				return "abstractEdgeTool"
			},
			getUrl : function() {
				return Gef.IMAGE_ROOT + "../../tools/edges.png"
			},
			getX : function($) {
				return $ + 5
			},
			getY : function() {
				return 40
			},
			drag : function(_) {
				var $ = this.node, A = $.editPart;
				if (A != null && A.getClass() == "node")
					if (A.canCreateOutgo())
						_.temp.editPart = A;
				_.getDraggingEdge().update(-1, -1, -1, -1)
			},
			move : function(_, C) {
				var B = C.point, $ = _.temp.editPart.getFigure(), D = {
					x : $.x,
					y : $.y,
					w : $.w,
					h : $.h
				}, A = _.getDraggingEdge();
				A.updateForDragging(D, B)
			},
			drop : function($, B) {
				var A = $.getDraggingEdge(), E = $.temp.editPart, C = $
						.findEditPartAt(B);
				if (E != C && E.canCreateOutgo(C) && C.getClass() == "node"
						&& C.canCreateIncome(E)) {
					var _ = this.getConnectionModelName(), D = $
							.getModelFactory().createModel(_);
					if (E.getModel().getOutgoingConnections().length > 0)
						D.text = "to " + C.getModel().text;
					else
						D.text = "";
					B.role = {
						name : "CREATE_EDGE",
						rect : {
							x1 : A.x1,
							y1 : A.y1,
							x2 : A.x2,
							y2 : A.y2
						},
						source : E.getModel(),
						target : C.getModel(),
						model : D
					};
					$.executeCommand($.temp.editPart, B)
				}
				$.getDraggingEdge().moveToHide()
			}
		});
Gef.ns("Gef.gef.xml");
Gef.gef.xml.XmlSerializer = function($) {
	this.model = $
};
Gef.gef.xml.XmlSerializer.prototype = {
	serialize : function() {
		return this.model.encode()
	}
};
Gef.ns("Gef.gef.xml");
Gef.gef.xml.XmlDeserializer = Gef.extend(Object, {
			constructor : function($) {
				this.xdoc = Gef.model.XmlUtil.readXml($)
			},
			decodeNodeModel : function(_, A, $) {
				_.decode(A, ["transition"]);
				Gef.model.JpdlUtil.decodeNodePosition(_);
				this.modelMap[_.text] = _;
				this.domMap[_.text] = A;
				$.addChild(_)
			}
		});
Gef.ns("Gef.layer");
Gef.layer.Layer = Gef.extend(Gef.figure.GroupFigure, {
			LAYER_MASK : "LAYER_MASK",
			LAYER_LABEL : "LAYER_LABEL",
			LAYER_DRAGGING : "LAYER_DRAGGING",
			LAYER_HANDLE : "LAYER_HANDLE",
			LAYER_NODE : "LAYER_NODE",
			LAYER_CONNECTION : "LAYER_CONNECTION",
			LAYER_SNAP : "LAYER_SNAP",
			LAYER_GRID : "LAYER_GRID",
			constructor : function($) {
				this.name = $;
				this.id = $;
				Gef.layer.Layer.superclass.constructor.call(this)
			},
			getName : function() {
				return this.name
			}
		});
Gef.ns("Gef.layer");
Gef.layer.GridLayer = Gef.extend(Gef.layer.Layer, {});
Gef.ns("Gef.model");
Gef.model.Model = Gef.extend(Object, {
			constructor : function($) {
				this.listeners = [];
				$ = $ ? $ : {};
				Gef.apply(this, $);
				this.createDom()
			},
			createDom : function() {
				this.dom = new Gef.model.Dom(this.getTagName())
			},
			getTagName : function() {
				return this.type
			},
			addChangeListener : function($) {
				this.listeners.push($)
			},
			removeChangeListener : function($) {
				this.listeners.remove($)
			},
			notify : function($, _) {
				for (var A = 0; A < this.listeners.length; A++)
					this.listeners[A].notifyChanged($, _)
			},
			getId : function() {
				if (this.id == null)
					this.id = Gef.id();
				return this.id
			},
			getType : function() {
				if (this.type == null)
					this.type = "node";
				return this.type
			},
			getEditPart : function() {
				return this.editPart
			},
			setEditPart : function($) {
				this.editPart = $
			}
		});
Gef.ns("Gef.model");
Gef.model.ModelChangeListener = Gef.extend(Object, {
			notifyChanged : Gef.emptyFn
		});
Gef.ns("Gef.model");
Gef.model.NodeModel = Gef.extend(Gef.model.Model, {
	CHILD_ADDED : "CHILD_ADDED",
	NODE_MOVED : "NODE_MOVED",
	NODE_RESIZED : "NODE_RESIZED",
	TEXT_UPDATED : "TEXT_UPDATED",
	CONNECTION_SOURCE_ADDED : "CONNECTION_SOURCE_ADDED",
	CONNECTION_TARGET_ADDED : "CONNECTION_TARGET_ADDED",
	CHILD_REMOVED_FROM_PARENT : "CHILD_REMOVED_FROM_PARENT",
	constructor : function($) {
		this.text = "untitled";
		this.x = 0;
		this.y = 0;
		this.w = 0;
		this.h = 0;
		this.children = [];
		this.outgoingConnections = [];
		this.incomingConnections = [];
		this.flowtype = null; //hey 新增流程类型属性
		Gef.model.NodeModel.superclass.constructor.call(this, $)
	},
	getText : function() {
		return this.text
	},
	setParent : function($) {
		this.parent = $
	},
	getParent : function() {
		return this.parent
	},
	setChildren : function($) {
		this.children = $
	},
	getChildren : function() {
		return this.children
	},
	addChild : function($) {
		this.children.push($);
		$.setParent(this);
		this.notify(this.CHILD_ADDED, $)
	},
	removeChild : function($) {
		this.children.remove($);
		$.setParent(null);
		$.notify("CHILD_REMOVED_FROM_PARENT", $)
	},
	getOutgoingConnections : function() {
		return this.outgoingConnections
	},
	getIncomingConnections : function() {
		return this.incomingConnections
	},
	addOutgoingConnection : function($) {
		if ($.getSource() == this && this.outgoingConnections.indexOf($) == -1) {
			this.outgoingConnections.push($);
			this.notify(this.CONNECTION_SOURCE_ADDED)
		}
	},
	addIncomingConnection : function($) {
		if ($.getTarget() == this && this.incomingConnections.indexOf($) == -1) {
			this.incomingConnections.push($);
			this.notify(this.CONNECTION_TARGET_ADDED)
		}
	},
	removeOutgoingConnection : function($) {
		if ($.getSource() == this && this.outgoingConnections.indexOf($) != -1)
			this.outgoingConnections.remove($)
	},
	removeIncomingConnection : function($) {
		if ($.getTarget() == this && this.incomingConnections.indexOf($) != -1)
			this.incomingConnections.remove($)
	},
	moveTo : function(_, $) {
		this.x = _;
		this.y = $;
		this.notify(this.NODE_MOVED)
	},
	resize : function(B, A, $, _) {
		this.x = B;
		this.y = A;
		this.w = $;
		this.h = _;
		this.notify(this.NODE_RESIZED)
	},
	updateText : function($) {
		this.text = $;
		this.notify(this.TEXT_UPDATED)
	},
	removeForParent : function() {
		if (!this.parent)
			return;
		this.parent.removeChild(this);
		this.notify(this.CHILD_REMOVED_FROM_PARENT)
	}
});
Gef.ns("Gef.model");
Gef.model.ConnectionModel = Gef.extend(Gef.model.Model, {
			RECONNECTED : "RECONNECTED",
			DISCONNECTED : "DISCONNECTED",
			CONNECTION_RESIZED : "CONNECTION_RESIZED",
			CONNECTION_TEXT_UPDATED : "CONNECTION_TEXT_UPDATED",
			TEXT_POSITION_UPDATED : "TEXT_POSITION_UPDATED",
			SOURCE_CHANGED : "SOURCE_CHANGED",
			TARGET_CHANGED : "TARGET_CHANGED",
			constructor : function($) {
				this.x1 = 0;
				this.y1 = 0;
				this.x2 = 0;
				this.y2 = 0;
				this.text = "untitled";
				this.textX = 0;
				this.textY = 0;
				this.innerPoints = [];
				Gef.model.ConnectionModel.superclass.constructor.call(this, $)
			},
			getText : function() {
				return this.text
			},
			getSource : function() {
				return this.source
			},
			setSource : function($) {
				this.source = $
			},
			getTarget : function() {
				return this.target
			},
			setTarget : function($) {
				this.target = $
			},
			reconnect : function() {
				this.notify(this.RECONNECTED);
				this.source.addOutgoingConnection(this);
				this.target.addIncomingConnection(this)
			},
			disconnect : function() {
				this.notify(this.DISCONNECTED);
				this.source.removeOutgoingConnection(this);
				this.target.removeIncomingConnection(this)
			},
			resizeConnection : function($) {
				this.innerPoints = $;
				this.notify(this.CONNECTION_RESIZED)
			},
			updateText : function($) {
				this.text = $;
				this.notify(this.CONNECTION_TEXT_UPDATED)
			},
			updateTextPosition : function(_, $) {
				this.textX = _;
				this.textY = $;
				this.notify(this.TEXT_POSITION_UPDATED)
			},
			changeSource : function($) {
				var _ = this.source;
				this.source = $;
				$.addOutgoingConnection(this);
				_.removeOutgoingConnection(this);
				this.notify(this.SOURCE_CHANGED, {
							newSource : $,
							oldSource : _
						})
			},
			changeTarget : function(_) {
				var $ = this.target;
				this.target = _;
				_.addIncomingConnection(this);
				$.removeIncomingConnection(this);
				this.notify(this.TARGET_CHANGED, {
							newTarget : _,
							oldTarget : $
						})
			}
		});
Gef.ns("Gef.model");
Gef.model.Dom = Gef.extend(Object, {
			constructor : function($) {
				if (typeof $ != "string" || Gef.isEmpty($)) {
					alert("Dom must specify a exist tagName");
					return
				}
				this.tagName = $;
				this.value = null;
				this.parent = null;
				this.step = "";
				this.attributes = {};
				this.elements = []
			},
			setAttribute : function($, _) {
				if (Gef.notEmpty(_))
					this.attributes[$] = _;
				else
					this.removeAttribute($)
			},
			removeAttribute : function($) {
				delete this.attributes[$]
			},
			hasAttribute : function($) {
				var _ = this.attributes[$];
				return Gef.notEmpty(_)
			},
			getAttribute : function($) {
				if (this.hasAttribute($))
					return this.attributes[$];
				else
					return ""
			},
			addElement : function($) {
				$.updateStep(this.step);
				this.elements.push($)
			},
			removeElement : function($) {
				this.elements.remove($)
			},
			getElementContent : function(_) {
				var $ = this.getElementByTagName(_);
				if ($)
					return $.value;
				else
					return ""
			},
			setElementContent : function(_, A) {
				var $ = this.getElementByTagName(_);
				if ($) {
					if (Gef.notEmpty(A))
						$.value = A;
					else
						this.elements.remove($)
				} else if (Gef.notEmpty(A)) {
					$ = new Gef.model.Dom(_);
					$.value = A;
					this.addElement($)
				}
			},
			getElementAttribute : function(_, A) {
				var $ = this.getElementByTagName(_);
				if ($ && $.hasAttribute(A))
					return $.getAttribute(A);
				else
					return ""
			},
			setElementAttribute : function(_, B, A) {
				var $ = this.getElementByTagName(_);
				if ($)
					$.setAttribute(B, A);
				else {
					$ = new Gef.model.Dom(_);
					$.setAttribute(B, A);
					this.addElement($)
				}
			},
			getElementByTagName : function(_) {
				for (var A = 0; A < this.elements.length; A++) {
					var $ = this.elements[A];
					if ($.tagName == _)
						return $
				}
				return null
			},
			getElementsByTagName : function(_) {
				var A = [];
				for (var B = 0; B < this.elements.length; B++) {
					var $ = this.elements[B];
					if ($.tagName == _)
						A.push($)
				}
				return A
			},
			getProperty : function($, A) {
				var _ = "", B = this.getElementsByTagName("property");
				Gef.each(B, function(B) {
							if (B.getAttribute("name") == $) {
								Gef.each(B.elements, function($) {
											if (A == "boolean")
												_ = ($.tagName === "true");
											else if (A == $.tagName)
												_ = $.getAttribute("value");
											return false
										});
								return false
							}
						});
				return _
			},
			setProperty : function(B, C, D) {
				if (Gef.isEmpty(C))
					return;
				var F = false, _ = null, E = this
						.getElementsByTagName("property");
				Gef.each(E, function($) {
							if ($.getAttribute("name") == B) {
								_ = $;
								return false
							}
						});
				if (_ == null) {
					_ = new Gef.model.Dom("property");
					_.setAttribute("name", B);
					this.addElement(_)
				}
				if (D == "boolean") {
					for (var G = _.elements.length - 1; G >= 0; G--) {
						var $ = _.elements[G];
						_.elements.remove($)
					}
					var A = (C == true) ? "true" : "false";
					_.addElement(new Gef.model.Dom(A))
				} else
					_.setElementAttribute(D, "value", C)
			},
			removeProperty : function($) {
				var _ = this.getElementsByTagName("property");
				Gef.each(_, function(_) {
							if (_.getAttribute("name") == $) {
								this.elements.remove(_);
								return false
							}
						}, this)
			},
			updateStep : function($) {
				for (var _ = 0; _ < $.length + 2; _++)
					this.step += " "
			},
			encode : function(C, A) {
				if (Gef.notEmpty(this.value)
						&& (Gef.notEmpty(C) || Gef.notEmpty(this.elements))) {
					alert("can not set insert xml into TextNode");
					return
				}
				if (Gef.isEmpty(C))
					C = "";
				var E = A ? A : "";
				A = E + this.step;
				var D = [A, "<", this.tagName];
				for (var F in this.attributes) {
					var B = this.attributes[F];
					D.push(" ", F, "='", this.encodeText(B), "'")
				}
				if (Gef.isEmpty(this.elements) && Gef.isEmpty(C)
						&& Gef.isEmpty(this.value))
					D.push("/>");
				else if (Gef.notEmpty(this.value))
					D.push(">", this.encodeText(this.value), "</",
							this.tagName, ">");
				else {
					D.push(">\n");
					for (var G = 0; G < this.elements.length; G++) {
						var _ = this.elements[G], $ = _.encode("", E);
						D.push($)
					}
					D.push(C);
					D.push(A, "</", this.tagName, ">")
				}
				D.push("\n");
				return D.join("")
			},
			encodeText : function($) {
				if (typeof $ != "")
					$ += "";
				return $.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(
						/>/g, "&gt;").replace(/\'/g, "&apos;").replace(/\"/g,
						"&quot;")
			},
			decode : function($, C) {
				C = C ? C : [];
				if (typeof $ == "string") {
					var A = Gef.model.XmlUtil.readXml(xml);
					$ = A.documentElement
				}
				this.tagName = $.nodeName;
				for (var F = 0; F < $.attributes.length; F++) {
					var D = $.attributes[F];
					this.setAttribute(D.name, D.nodeValue)
				}
				if ($.childNodes.length == 1 && $.childNodes[0].nodeType == 3)
					this.value = $.childNodes[0].nodeValue;
				else {
					var E = Gef.model.XmlUtil.elements($);
					for (F = 0; F < E.length; F++) {
						var _ = E[F];
						if (C.indexOf(_.tagName) != -1)
							continue;
						var B = new Gef.model.Dom("node");
						B.decode(_);
						this.addElement(B)
					}
				}
			}
		});
Gef.ns("Gef.model");
Gef.model.XmlUtil = {
	readXml : function(_) {
		var $ = null;
		if (typeof(DOMParser) == "undefined") {
			$ = new ActiveXObject("Microsoft.XMLDOM");
			$.async = "false";
			$.loadXML(_)
		} else {
			var A = new DOMParser();
			$ = A.parseFromString(_, "application/xml");
			A = null
		}
		if ($.documentElement == null)
			alert("import error");
		else if ($.documentElement.tagName == "parsererror")
			alert("import error: " + $.documentElement.firstChild.textContent);
		else
			return $
	},
	elements : function($) {
		var A = [];
		for (var B = 0; B < $.childNodes.length; B++) {
			var _ = $.childNodes[B];
			if (_.nodeType != 3 && _.nodeType != 8)
				A.push(_)
		}
		return A
	},
	decode : function(_) {
		var $ = new Gef.model.Dom("node");
		$.decode(_);
		return $
	}
};
Gef.ns("Gef.model");
Gef.model.JpdlUtil = {
	decodeNodePosition : function(_) {
		var $ = _.dom.getAttribute("g"), A = $.split(",");
		_.x = parseInt(A[0], 10);
		_.y = parseInt(A[1], 10);
		_.w = parseInt(A[2], 10);
		_.h = parseInt(A[3], 10)
	},
	decodeConnectionPosition : function(D) {
		var $ = D.dom.getAttribute("g");
		if (!$)
			return;
		var C = $, A = $.split(":");
		if ($.indexOf(":") != -1) {
			C = A[1];
			if (A[0].length > 0) {
				var E = A[0].split(";"), B = [];
				Gef.each(E, function($) {
							var _ = $.split(",");
							B.push([parseInt(_[0], 10), parseInt(_[1], 10)])
						});
				D.innerPoints = B
			}
		} else
			C = $;
		var _ = C.split(",");
		D.textX = parseInt(_[0], 10);
		D.textY = parseInt(_[1], 10);
		this.decodeTextPosition(D)
	},
	decodeTextPosition : function(J) {
		var P = J.getSource(), K = new Geom.Rect(parseInt(P.x, 10), parseInt(
						P.y, 10), parseInt(P.w, 10), parseInt(P.h, 10)), N = J
				.getTarget(), I = new Geom.Rect(parseInt(N.x, 10), parseInt(
						N.y, 10), parseInt(N.w, 10), parseInt(N.h, 10)), $ = new Geom.Line(
				parseInt(K.x, 10) + parseInt(K.w, 10) / 2, parseInt(K.y, 10)
						+ parseInt(K.h, 10) / 2, parseInt(I.x, 10)
						+ parseInt(I.w, 10) / 2, parseInt(I.y, 10)
						+ parseInt(I.h, 10) / 2), E = K.getCrossPoint($), C = I
				.getCrossPoint($);
		if ((!E) || (!C))
			return;
		var L = (E.x + C.x) / 2, B = (E.y + C.y) / 2;
		if (J.innerPoints.length > 0) {
			var A = J.innerPoints[0], _ = J.innerPoints[J.innerPoints.length
					- 1], O = [];
			O.push([E.x, E.y]);
			Gef.each(J.innerPoints, function($) {
						O.push([$[0], $[1]])
					});
			O.push([C.x, C.y]);
			var G = O.length, F = 0, D = 0;
			if ((G % 2) == 0) {
				var H = O[G / 2 - 1], M = O[G / 2];
				F = (H[0] + M[0]) / 2;
				D = (H[1] + M[1]) / 2
			} else {
				F = O[(G - 1) / 2][0];
				D = O[(G - 1) / 2][1]
			}
			var R = parseInt(J.textX + L - F, 10), Q = parseInt(
					J.textY + B - D, 10);
			J.textX -= L - F;
			J.textY -= B - D
		}
	},
	encodeNodePosition : function($) {
	$.dom.setAttribute("g", parseInt($.x) + "," + parseInt($.y) + "," + parseInt($.w) + "," + parseInt($.h))
	},
	encodeConnectionPosition : function(_) {
		var $ = [];
		Gef.each(_.innerPoints, function(B, A) {
					$.push(parseInt(B[0], 10), ",", parseInt(B[1], 10));
					if (A != _.innerPoints.length - 1)
						$.push(";")
				});
		$.push(this.encodeTextPosition(_));
		return $.join("")
	},
	encodeTextPosition : function(J) {
		var P = J.getSource(), K = new Geom.Rect(parseInt(P.x, 10), parseInt(
						P.y, 10), parseInt(P.w, 10), parseInt(P.h, 10)), N = J
				.getTarget(), I = new Geom.Rect(parseInt(N.x, 10), parseInt(
						N.y, 10), parseInt(N.w, 10), parseInt(N.h, 10)), $ = new Geom.Line(
				parseInt(K.x, 10) + parseInt(K.w, 10) / 2, parseInt(K.y, 10)
						+ parseInt(K.h, 10) / 2, parseInt(I.x, 10)
						+ parseInt(I.w, 10) / 2, parseInt(I.y, 10)
						+ parseInt(I.h, 10) / 2), E = K.getCrossPoint($), C = I
				.getCrossPoint($);
		if ((!E) || (!C))
			return;
		var L = (E.x + C.x) / 2, B = (E.y + C.y) / 2;
		if (J.innerPoints.length > 0) {
			var A = J.innerPoints[0], _ = J.innerPoints[J.innerPoints.length
					- 1], O = [];
			O.push([E.x, E.y]);
			Gef.each(J.innerPoints, function($) {
						O.push([$[0], $[1]])
					});
			O.push([C.x, C.y]);
			var G = O.length, F = 0, D = 0;
			if ((G % 2) == 0) {
				var H = O[G / 2 - 1], M = O[G / 2];
				F = (H[0] + M[0]) / 2;
				D = (H[1] + M[1]) / 2
			} else {
				F = O[(G - 1) / 2][0];
				D = O[(G - 1) / 2][1]
			}
			var R = parseInt(J.textX + L - F, 10), Q = parseInt(
					J.textY + B - D, 10);
			return ":" + R + "," + Q
		} else if (J.textX != 0 && J.textY != 0)
			return parseInt(J.textX, 10) + "," + parseInt(J.textY, 10);
		else
			return ""
	}
};
Gef.ns("Gef.jbs");
Gef.jbs.JBSEditPartFactory = Gef.extend(Gef.gef.EditPartFactory, {
			createEditPart : function(type) {
				return Gef.jbs.JBSEditPartFactory._editPartLib[type]
						? eval("new "
								+ Gef.jbs.JBSEditPartFactory._editPartLib[type]
								+ "(type)")
						: null
			}
		});
Gef.jbs.JBSEditPartFactory._editPartLib = {
	"process" : "Gef.jbs.editpart.ProcessEditPart",
	"start" : "Gef.jbs.editpart.StartEditPart",
	"end" : "Gef.jbs.editpart.EndEditPart",
	"cancel" : "Gef.jbs.editpart.CancelEditPart",
	"error" : "Gef.jbs.editpart.ErrorEditPart",
	"state" : "Gef.jbs.editpart.StateEditPart",
	"hql" : "Gef.jbs.editpart.HqlEditPart",
	"sql" : "Gef.jbs.editpart.SqlEditPart",
	"mission" : "Gef.jbs.editpart.MissionEditPart",
	"idea" : "Gef.jbs.editpart.IdeaEditPart",
	"java" : "Gef.jbs.editpart.JavaEditPart",
	"script" : "Gef.jbs.editpart.ScriptEditPart",
	"task" : "Gef.jbs.editpart.TaskEditPart",
	"decision" : "Gef.jbs.editpart.DecisionEditPart",
	"fork" : "Gef.jbs.editpart.ForkEditPart",
	"join" : "Gef.jbs.editpart.JoinEditPart",
	"custom" : "Gef.jbs.editpart.CustomEditPart",
	"mail" : "Gef.jbs.editpart.MailEditPart",
	"subProcess" : "Gef.jbs.editpart.SubProcessEditPart",
	"group" : "Gef.jbs.editpart.GroupEditPart",
	"jms" : "Gef.jbs.editpart.JmsEditPart",
	"ruleDecision" : "Gef.jbs.editpart.RuleDecisionEditPart",
	"rules" : "Gef.jbs.editpart.RulesEditPart",
	"auto" : "Gef.jbs.editpart.AutoEditPart",
	"human" : "Gef.jbs.editpart.HumanEditPart",
	"counter-sign" : "Gef.jbs.editpart.CounterSignEditPart",
	"foreach" : "Gef.jbs.editpart.ForeachEditPart",
	"transition" : "Gef.jbs.editpart.TransitionEditPart"
};
Gef.jbs.JBSEditPartFactory.registerEditPart = function(_, $) {
	Gef.jbs.JBSEditPartFactory._editPartLib[_] = $
};
Gef.ns("Gef.jbs");
Gef.jbs.JBSModelFactory = Gef.extend(Gef.gef.ModelFactory, {
			getId : function($) {
				if (this.map == null)
					this.map = {};
				if (this.map[$] == null)
					this.map[$] = 1;
				else
					this.map[$]++;
				return $ + " " + this.map[$]
			},
			getTypeName : function($) {
				return $
			},
			reset : function() {
				delete this.map;
				this.map = {}
			},
			createModel : function(type) {
				var id = this.getId(type);
				return Gef.jbs.JBSModelFactory._modelLib[type] ? eval("new "
						+ Gef.jbs.JBSModelFactory._modelLib[type]
						+ "({id:id,text:id})") : null
			}
		});
Gef.jbs.JBSModelFactory._modelLib = {
	"process" : "Gef.jbs.model.ProcessModel",
	"start" : "Gef.jbs.model.StartModel",
	"end" : "Gef.jbs.model.EndModel",
	"cancel" : "Gef.jbs.model.CancelModel",
	"error" : "Gef.jbs.model.ErrorModel",
	"state" : "Gef.jbs.model.StateModel",
	"hql" : "Gef.jbs.model.HqlModel",
	"sql" : "Gef.jbs.model.SqlModel",
	"derive" : "Gef.jbs.model.DeriveModel",
	"idea" : "Gef.jbs.model.IdeaModel",
	"mission" : "Gef.jbs.model.MissionModel",
	"java" : "Gef.jbs.model.JavaModel",
	"script" : "Gef.jbs.model.ScriptModel",
	"task" : "Gef.jbs.model.TaskModel",
	"decision" : "Gef.jbs.model.DecisionModel",
	"fork" : "Gef.jbs.model.ForkModel",
	"join" : "Gef.jbs.model.JoinModel",
	"custom" : "Gef.jbs.model.CustomModel",
	"mail" : "Gef.jbs.model.MailModel",
	"subProcess" : "Gef.jbs.model.SubProcessModel",
	"group" : "Gef.jbs.model.GroupModel",
	"transition" : "Gef.jbs.model.TransitionModel",
	"jms" : "Gef.jbs.model.JmsModel",
	"ruleDecision" : "Gef.jbs.model.RuleDecisionModel",
	"rules" : "Gef.jbs.model.RulesModel",
	"auto" : "Gef.jbs.model.AutoModel",
	"human" : "Gef.jbs.model.HumanModel",
	"counter-sign" : "Gef.jbs.model.CounterSignModel",
	"foreach" : "Gef.jbs.model.ForeachModel"
};
Gef.jbs.JBSModelFactory.registerModel = function(_, $) {
	Gef.jbs.JBSModelFactory._modelLib[_] = $
};
Gef.ns("Gef.jbs");
Gef.jbs.JBSPaletteHelper = Gef.extend(Gef.gef.support.PaletteHelper, {
			constructor : function($) {
				this.editor = $
			},
			createSource : function() {
				var $ = this;
				return {
					title : "palette",
					buttons : [{
								text : "export",
								handler : function() {
									alert($.editor.serial())
								}
							}, {
								text : "clear",
								handler : function() {
									$.editor.clear()
								}
							}, {
								text : "reset",
								handler : function() {
									$.editor.reset()
								}
							}],
					groups : [{
								title : "Operations",
								items : [{
											text : "Select",
											iconCls : "gef-tool-select",
											creatable : false
										}, {
											text : "Marquee",
											iconCls : "gef-tool-marquee",
											creatable : false
										}]
							}, {
								title : "Activities",
								items : [{
											text : "transition",
											iconCls : "gef-tool-transition",
											creatable : false,
											isConnection : true
										}, {
											text : "auto",
											iconCls : "gef-tool-java",
											w : 90,
											h : 50
										}, {
											text : "human",
											iconCls : "gef-tool-task",
											w : 90,
											h : 50
										}, {
											text : "counter-sign",
											iconCls : "gef-tool-task",
											w : 90,
											h : 50
										}, {
											text : "start",
											iconCls : "gef-tool-start",
											w : 48,
											h : 48
										}, {
											text : "end",
											iconCls : "gef-tool-end",
											w : 48,
											h : 48
										}, {
											text : "cancel",
											iconCls : "gef-tool-cancel",
											w : 48,
											h : 48
										}, {
											text : "error",
											iconCls : "gef-tool-error",
											w : 48,
											h : 48
										}, {
											text : "state",
											iconCls : "gef-tool-state",
											w : 90,
											h : 50
										}, {
											text : "hql",
											iconCls : "gef-tool-hql",
											w : 90,
											h : 50
										}, {
											text : "sql",
											iconCls : "gef-tool-sql",
											w : 90,
											h : 50
										}, {
											text : "java",
											iconCls : "gef-tool-java",
											w : 90,
											h : 50
										}, {
											text : "script",
											iconCls : "gef-tool-script",
											w : 90,
											h : 50
										}, {
											text : "task",
											iconCls : "gef-tool-task",
											w : 90,
											h : 50
										}, {
											text : "decision",
											iconCls : "gef-tool-decision",
											w : 48,
											h : 48
										}, {
											text : "fork",
											iconCls : "gef-tool-fork",
											w : 48,
											h : 48
										}, {
											text : "join",
											iconCls : "gef-tool-join",
											w : 48,
											h : 48
										}, {
											text : "mail",
											iconCls : "gef-tool-mail",
											w : 90,
											h : 50
										}, {
											text : "custom",
											iconCls : "gef-tool-custom",
											w : 90,
											h : 50
										}, {
											text : "subProcess",
											iconCls : "gef-tool-subProcess",
											w : 90,
											h : 50
										}, {
											text : "group",
											iconCls : "gef-tool-group",
											w : 90,
											h : 50
										}, {
											text : "jms",
											iconCls : "gef-tool-jms",
											w : 90,
											h : 50
										}, {
											text : "ruleDecision",
											iconCls : "gef-tool-ruleDecision",
											w : 48,
											h : 48
										}, {
											text : "rules",
											iconCls : "gef-tool-rules",
											w : 90,
											h : 50
										}, {
											text : "foreach",
											iconCls : "gef-tool-foreach",
											w : 48,
											h : 48
										}]
							}]
				}
			},
			getSource : function() {
				if (!this.source)
					this.source = this.createSource();
				return this.source
			},
			render : function(O) {
				var C = this.getSource(), K = document.createElement("div");
				K.className = "gef-drag-handle";
				O.appendChild(K);
				var $ = document.createElement("span");
				K.appendChild($);
				$.unselectable = "on";
				$.innerHTML = C.title;
				var L = this;
				for (var F = 0; F < C.buttons.length; F++) {
					var I = C.buttons[F], _ = document.createElement("a");
					_.href = "javascript:void(0);";
					_.onclick = I.handler;
					_.innerHTML = "|" + I.text + "|";
					$.appendChild(_)
				}
				var A = document.createElement("ul");
				O.appendChild(A);
				for (F = 0; F < C.groups.length; F++) {
					var M = C.groups[F], D = document.createElement("li");
					D.className = "gef-palette-bar";
					A.appendChild(D);
					var H = document.createElement("div");
					H.unselectable = "on";
					H.innerHTML = M.title;
					D.appendChild(H);
					var N = document.createElement("ul");
					D.appendChild(N);
					for (var E = 0; E < M.items.length; E++) {
						var J = M.items[E], G = document.createElement("li");
						G.id = J.text;
						G.className = "gef-palette-item";
						N.appendChild(G);
						var B = document.createElement("span");
						B.innerHTML = J.text;
						B.className = J.iconCls;
						B.unselectable = "on";
						G.appendChild(B)
					}
				}
			},
			getActivePalette : function() {
				return this.activePalette
			},
			setActivePalette : function($) {
				this.activePalette = $
			},
			getPaletteConfig : function(D, _) {
				var $ = _.parentNode.id;
				if (!$)
					return null;
				var B = this.getSource(), E = null;
				Gef.each(B.groups, function(_) {
							Gef.each(_.items, function(_) {
										if (_.text == $) {
											E = _;
											return false
										}
									});
							if (E != null)
								return false
						});
				if (!E)
					return null;
				var A = null;
				if (this.getActivePalette()) {
					var C = this.getActivePalette().text;
					A = document.getElementById(C);
					A.style.background = "white"
				}
				this.setActivePalette(E);
				A = document.getElementById($);
				A.style.background = "#CCCCCC";
				if (E.creatable === false)
					return null;
				return E
			}
		});
Gef.ns("Gef.jbs");
Gef.jbs.JBSEditor = Gef.extend(
		Gef.gef.support.DefaultGraphicalEditorWithPalette, {
			constructor : function() {
				this.modelFactory = new Gef.jbs.JBSModelFactory();
				this.editPartFactory = new Gef.jbs.JBSEditPartFactory();
				Gef.jbs.JBSEditor.superclass.constructor.call(this)
			},
			getPaletteHelper : function() {
				if (!this.paletteHelper)
					this.paletteHelper = new Gef.jbs.JBSPaletteHelper(this);
				return this.paletteHelper
			},
			serial : function() {
				var $ = this.getGraphicalViewer().getContents().getModel(), _ = new Gef.jbs.xml.JBSSerializer($), A = _.serialize();
				return A
			},
			clear : function() {
				var D = this.getGraphicalViewer(), A = D.getContents(), C = D
						.getBrowserListener(), _ = D.getEditDomain()
						.getCommandStack(), $ = C.getSelectionManager();
				$.selectAll();
				var B = A.getRemoveNodesCommand({
							role : {
								nodes : $.getSelectedNodes()
							}
						});
				_.execute(B);
				$.clearAll();
				this.editDomain.editPartRegistry = []
			},
			reset : function() {
				this.clear();
				var A = this.getGraphicalViewer(), $ = A.getEditDomain()
						.getCommandStack();
				$.flush();
				this.getModelFactory().reset();
				var _ = A.getContents();
				_.text = "untitled";
				_.key = null;
				_.description = null
			},
			resetAndOpen : function($) {
				this.reset();
			   	var A = new Gef.jbs.xml.JBSDeserializer($), _ = A.decode();
				this.getGraphicalViewer().setContents(_);
				this.updateModelFactory();
				this.getGraphicalViewer().getContents().refresh()
			}
		});
Gef.ns("Gef.jbs");
Gef.jbs.JBSEditorInput = Gef.extend(Gef.ui.EditorInput, {
			constructor : function($) {
				if (!$)
					$ = "process";
				this.processModel = $
			},
			readXml : function($) {
				var _ = new Gef.jbs.xml.JBSDeserializer($);
				this.processModel = _.decode()
			},
			getName : function() {
				return this.processModel.name
			},
			getObject : function() {
				return this.processModel
			}
		});
Gef.ns("Gef.jbs.editpart");
Gef.jbs.editpart.ProcessEditPart = Gef.extend(Gef.gef.editparts.NodeEditPart, {
			createFigure : function() {
				return new Gef.jbs.figure.ProcessFigure()
			},
			getClass : function() {
				return "process"
			},
			canCreate : function($) {
				var _ = true;
				if ($.getType() == "start")
					Gef.each(this.children, function($) {
								if ($.getModel().type == "start") {
									Gef.showMessage("validate.only_one_start",
											"cannot have more than one START.");
									_ = false;
									return false
								}
							});
				return _
			}
		});
Gef.ns("Gef.jbs.editpart");
Gef.jbs.editpart.StartEditPart = Gef.extend(Gef.gef.editparts.NodeEditPart, {
			createFigure : function() {
				var $ = this.getModel(), _ = new Gef.jbs.figure.StartFigure({
							x : this.model.x,
							y : this.model.y,
							name : this.model.text
						});
				_.editPart = this;
				return _
			},
			canCreateOutgo : function() {
				if (this.getOutgoingConnections().length == 0)
					return true;
				else {
					Gef.showMessage("validate.start_only_one_outgo",
							"START could have only one outgo transition.");
					return false
				}
			},
			canCreateIncome : function() {
				Gef.showMessage("validate.start_no_income",
						"START could not have income transition.");
				return false
			},
			canResize : function() {
				return false
			}
		});
Gef.ns("Gef.jbs.editpart");
Gef.jbs.editpart.EndEditPart = Gef.extend(Gef.gef.editparts.NodeEditPart, {
			createFigure : function() {
				var $ = this.getModel(), _ = new Gef.jbs.figure.EndFigure({
							x : this.model.x,
							y : this.model.y,
							name : this.model.text
						});
				_.editPart = this;
				return _
			},
			canCreateOutgo : function() {
				Gef.showMessage("validate.end_no_outgo",
						"END could not have outgo transition.");
				return false
			},
			canResize : function() {
				return false
			}
		});
Gef.ns("Gef.jbs.editpart");
Gef.jbs.editpart.CancelEditPart = Gef.extend(Gef.gef.editparts.NodeEditPart, {
			createFigure : function() {
				var $ = this.getModel(), _ = new Gef.jbs.figure.CancelFigure({
							x : this.model.x,
							y : this.model.y,
							name : this.model.text
						});
				_.editPart = this;
				return _
			},
			canCreateOutgo : function() {
				Gef.showMessage("validate.cancel_no_outgo",
						"CANCEL could not have outgo transition.");
				return false
			},
			canResize : function() {
				return false
			}
		});
Gef.ns("Gef.jbs.editpart");
Gef.jbs.editpart.ErrorEditPart = Gef.extend(Gef.gef.editparts.NodeEditPart, {
			createFigure : function() {
				var $ = this.getModel(), _ = new Gef.jbs.figure.ErrorFigure({
							x : this.model.x,
							y : this.model.y,
							name : this.model.text
						});
				_.editPart = this;
				return _
			},
			canCreateOutgo : function() {
				Gef.showMessage("validate.error_no_outgo",
						"ERROR could noe have outgo transition.");
				return false
			},
			canResize : function() {
				return false
			}
		});
Gef.ns("Gef.jbs.editpart");
Gef.jbs.editpart.StateEditPart = Gef.extend(Gef.gef.editparts.NodeEditPart, {
			createFigure : function() {
				var $ = this.getModel(), _ = new Gef.jbs.figure.StateFigure({
							x : this.model.x,
							y : this.model.y,
							name : this.model.text
						});
				_.editPart = this;
				return _
			}
		});
Gef.ns("Gef.jbs.editpart");
Gef.jbs.editpart.HqlEditPart = Gef.extend(Gef.gef.editparts.NodeEditPart, {
			createFigure : function() {
				var $ = this.getModel(), _ = new Gef.jbs.figure.HqlFigure({
							x : this.model.x,
							y : this.model.y,
							name : this.model.text
						});
				_.editPart = this;
				return _
			}
		});
Gef.ns("Gef.jbs.editpart");
Gef.jbs.editpart.SqlEditPart = Gef.extend(Gef.gef.editparts.NodeEditPart, {
			createFigure : function() {
				var $ = this.getModel(), _ = new Gef.jbs.figure.SqlFigure({
							x : this.model.x,
							y : this.model.y,
							name : this.model.text
						});
				_.editPart = this;
				return _
			}
		});
Gef.ns("Gef.jbs.editpart");
Gef.jbs.editpart.IdeaEditPart = Gef.extend(Gef.gef.editparts.NodeEditPart, {
			createFigure : function() {
				var $ = this.getModel(), _ = new Gef.jbs.figure.IdeaFigure({
							x : this.model.x,
							y : this.model.y,
							name : this.model.text
						});
				_.editPart = this;
				return _
			}
		});
Gef.ns("Gef.jbs.editpart");
Gef.jbs.editpart.MissionEditPart = Gef.extend(Gef.gef.editparts.NodeEditPart, {
			createFigure : function() {
				var $ = this.getModel(), _ = new Gef.jbs.figure.MissionFigure({
							x : this.model.x,
							y : this.model.y,
							name : this.model.text
						});
				_.editPart = this;
				return _
			}
		});
Gef.ns("Gef.jbs.editpart");
Gef.jbs.editpart.JavaEditPart = Gef.extend(Gef.gef.editparts.NodeEditPart, {
			createFigure : function() {
				var $ = this.getModel(), _ = new Gef.jbs.figure.JavaFigure({
							x : this.model.x,
							y : this.model.y,
							name : this.model.text
						});
				_.editPart = this;
				return _
			}
		});
Gef.ns("Gef.jbs.editpart");
Gef.jbs.editpart.ScriptEditPart = Gef.extend(Gef.gef.editparts.NodeEditPart, {
			createFigure : function() {
				var $ = this.getModel(), _ = new Gef.jbs.figure.ScriptFigure({
							x : this.model.x,
							y : this.model.y,
							name : this.model.text
						});
				_.editPart = this;
				return _
			}
		});
Gef.ns("Gef.jbs.editpart");
Gef.jbs.editpart.TaskEditPart = Gef.extend(Gef.gef.editparts.NodeEditPart, {
			createFigure : function() {
				var $ = this.getModel(), _ = new Gef.jbs.figure.TaskFigure({
							x : this.model.x,
							y : this.model.y,
							name : this.model.text,
							flowtype : this.model.flowtype
						});
				_.editPart = this;
				return _
			}
		});
Gef.ns("Gef.jbs.editpart");
Gef.jbs.editpart.DecisionEditPart = Gef.extend(Gef.gef.editparts.NodeEditPart,
		{
			createFigure : function() {
				var $ = this.getModel(), _ = new Gef.jbs.figure.DecisionFigure(
						{
							x : this.model.x,
							y : this.model.y,
							name : this.model.text
						});
				_.editPart = this;
				return _
			},
			canResize : function() {
				return false
			}
		});
Gef.ns("Gef.jbs.editpart");
Gef.jbs.editpart.ForkEditPart = Gef.extend(Gef.gef.editparts.NodeEditPart, {
			createFigure : function() {
				var $ = this.getModel(), _ = new Gef.jbs.figure.ForkFigure({
							x : this.model.x,
							y : this.model.y,
							name : this.model.text
						});
				_.editPart = this;
				return _
			},
			canResize : function() {
				return false
			}
		});
Gef.ns("Gef.jbs.editpart");
Gef.jbs.editpart.JoinEditPart = Gef.extend(Gef.gef.editparts.NodeEditPart, {
			createFigure : function() {
				var $ = this.getModel(), _ = new Gef.jbs.figure.JoinFigure({
							x : this.model.x,
							y : this.model.y,
							name : this.model.text
						});
				_.editPart = this;
				return _
			},
			canResize : function() {
				return false
			}
		});
Gef.ns("Gef.jbs.editpart");
Gef.jbs.editpart.CustomEditPart = Gef.extend(Gef.gef.editparts.NodeEditPart, {
			createFigure : function() {
				var $ = this.getModel(), _ = new Gef.jbs.figure.CustomFigure({
							x : this.model.x,
							y : this.model.y,
							name : this.model.text
						});
				_.editPart = this;
				return _
			}
		});
Gef.ns("Gef.jbs.editpart");
Gef.jbs.editpart.MailEditPart = Gef.extend(Gef.gef.editparts.NodeEditPart, {
			createFigure : function() {
				var $ = this.getModel(), _ = new Gef.jbs.figure.MailFigure({
							x : this.model.x,
							y : this.model.y,
							name : this.model.text
						});
				_.editPart = this;
				return _
			}
		});
Gef.ns("Gef.jbs.editpart");
Gef.jbs.editpart.SubProcessEditPart = Gef.extend(
		Gef.gef.editparts.NodeEditPart, {
			createFigure : function() {
				var $ = this.getModel(), _ = new Gef.jbs.figure.SubProcessFigure(
						{
							x : this.model.x,
							y : this.model.y,
							name : this.model.text
						});
				_.editPart = this;
				return _
			}
		});
Gef.ns("Gef.jbs.editpart");
Gef.jbs.editpart.GroupEditPart = Gef.extend(Gef.gef.editparts.NodeEditPart, {
			createFigure : function() {
				var $ = this.getModel(), _ = new Gef.jbs.figure.GroupNodeFigure(
						{
							x : this.model.x,
							y : this.model.y,
							name : this.model.text
						});
				_.editPart = this;
				return _
			}
		});
Gef.ns("Gef.jbs.editpart");
Gef.jbs.editpart.TransitionEditPart = Gef.extend(
		Gef.gef.editparts.ConnectionEditPart, {
			id:'transition',
			createFigure : function() {
				var $ = this.getModel(), _ = new Gef.jbs.figure.TransitionFigure(
						this.getSource().getFigure(), this.getTarget()
								.getFigure());
				_.innerPoints = $.innerPoints;
				_.name = $.text;
				if(_.name.indexOf("cancel")>0) _.name='不同意';
				if(_.name.indexOf("end")>0) _.name='同意';
				_.textX = $.textX;
				_.textY = $.textY;
				_.editPart = this;
				_.conditional = $.condition ? true : false;
				return _
			}
		});
Gef.ns("Gef.jbs.editpart");
Gef.jbs.editpart.JmsEditPart = Gef.extend(Gef.gef.editparts.NodeEditPart, {
			createFigure : function() {
				var $ = this.getModel(), _ = new Gef.jbs.figure.JmsFigure({
							x : this.model.x,
							y : this.model.y,
							name : this.model.text
						});
				_.editPart = this;
				return _
			}
		});
Gef.ns("Gef.jbs.editpart");
Gef.jbs.editpart.RuleDecisionEditPart = Gef.extend(
		Gef.gef.editparts.NodeEditPart, {
			createFigure : function() {
				var $ = this.getModel(), _ = new Gef.jbs.figure.RuleDecisionFigure(
						{
							x : this.model.x,
							y : this.model.y,
							name : this.model.text
						});
				_.editPart = this;
				return _
			},
			canResize : function() {
				return false
			}
		});
Gef.ns("Gef.jbs.editpart");
Gef.jbs.editpart.RulesEditPart = Gef.extend(Gef.gef.editparts.NodeEditPart, {
			createFigure : function() {
				var $ = this.getModel(), _ = new Gef.jbs.figure.RulesFigure({
							x : this.model.x,
							y : this.model.y,
							name : this.model.text
						});
				_.editPart = this;
				return _
			}
		});
Gef.ns("Gef.jbs.editpart");
Gef.jbs.editpart.AutoEditPart = Gef.extend(Gef.gef.editparts.NodeEditPart, {
			createFigure : function() {
				var $ = this.getModel(), _ = new Gef.jbs.figure.AutoFigure({
							x : this.model.x,
							y : this.model.y,
							name : this.model.text
						});
				_.editPart = this;
				return _
			}
		});
Gef.ns("Gef.jbs.editpart");
Gef.jbs.editpart.HumanEditPart = Gef.extend(Gef.gef.editparts.NodeEditPart, {
			createFigure : function() {
				var $ = this.getModel(), _ = new Gef.jbs.figure.HumanFigure({
							x : this.model.x,
							y : this.model.y,
							name : this.model.text
						});
				_.editPart = this;
				return _
			}
		});
Gef.ns("Gef.jbs.editpart");
Gef.jbs.editpart.CounterSignEditPart = Gef.extend(
		Gef.gef.editparts.NodeEditPart, {
			createFigure : function() {
				var $ = this.getModel(), _ = new Gef.jbs.figure.CounterSignFigure(
						{
							x : this.model.x,
							y : this.model.y,
							name : this.model.text
						});
				_.editPart = this;
				return _
			}
		});
Gef.ns("Gef.jbs.editpart");
Gef.jbs.editpart.ForeachEditPart = Gef.extend(Gef.gef.editparts.NodeEditPart, {
			createFigure : function() {
				var $ = this.getModel(), _ = new Gef.jbs.figure.ForeachFigure({
							x : this.model.x,
							y : this.model.y,
							name : this.model.text
						});
				_.editPart = this;
				return _
			},
			canResize : function() {
				return false
			}
		});
Gef.ns("Gef.jbs.model");
Gef.jbs.model.ProcessModel = Gef.extend(Gef.model.NodeModel, {
			type : "process",
			encode : function($) {
				var _ = "";
				Gef.each(this.children, function($) {
							_ += $.encode("", "  ")
						});
				this.dom.tagName = "process";
				var k= Gef.activeEditor.getGraphicalViewer().getRootEditPart().getContents().getModel();//zhouy 2012-08-27
				this.dom.setAttribute("name",Gef.activeEditor.getGraphicalViewer().getRootEditPart().getContents().getModel().pr_defname);
				this.dom.setAttribute("xmlns", "http://jbpm.org/4.4/jpdl");
				return this.dom.encode(_)
			},
			decode : function($, _) {
				this.dom.decode($, _);
				this.text = Gef.PROCESS_NAME;
				this.dom.removeAttribute("version");
				this.dom.setAttribute("name", Gef.PROCESS_NAME);
				this.dom.setAttribute("key", Gef.PROCESS_KEY)
			}
		});
Gef.ns("Gef.jbs.model");
Gef.jbs.model.StartModel = Gef.extend(Gef.model.NodeModel, {
			type : "start",
			encode : function(_, $) {
				var A = "";
				Gef.each(this.outgoingConnections, function(_) {
							A += _.encode("", $ + "  ")
						});
				Gef.model.JpdlUtil.encodeNodePosition(this);
				this.dom.setAttribute("name", this.text);
				return this.dom.encode(A, $)
			},
			decode : function($, _) {
				this.dom.decode($, _);
				this.text = this.dom.getAttribute("name")
			}
		});
Gef.ns("Gef.jbs.model");
Gef.jbs.model.EndModel = Gef.extend(Gef.model.NodeModel, {
			type : "end",
			encode : function(_, $) {
				var A = "";
				Gef.each(this.outgoingConnections, function(_) {
							A += _.encode("", $ + "  ")
						});
				Gef.model.JpdlUtil.encodeNodePosition(this);
				this.dom.setAttribute("name", this.text);
				return this.dom.encode(A, $)
			},
			decode : function($, _) {
				this.dom.decode($, _);
				this.text = this.dom.getAttribute("name")
			}
		});
Gef.ns("Gef.jbs.model");
Gef.jbs.model.CancelModel = Gef.extend(Gef.model.NodeModel, {
			type : "cancel",
			getTagName : function() {
				return "cancel"
			},
			encode : function(_, $) {
				var A = "";
				Gef.each(this.outgoingConnections, function(_) {
							A += _.encode("", $ + "  ")
						});
				Gef.model.JpdlUtil.encodeNodePosition(this);
				this.dom.setAttribute("name", this.text);
				return this.dom.encode(A, $)
			},
			decode : function($, _) {
				this.dom.decode($, _);
				this.text = this.dom.getAttribute("name")
			}
		});
Gef.ns("Gef.jbs.model");
Gef.jbs.model.ErrorModel = Gef.extend(Gef.model.NodeModel, {
			type : "error",
			getTagName : function() {
				return "error"
			},
			encode : function(_, $) {
				var A = "";
				Gef.each(this.outgoingConnections, function(_) {
							A += _.encode("", $ + "  ")
						});
				Gef.model.JpdlUtil.encodeNodePosition(this);
				this.dom.setAttribute("name", this.text);
				return this.dom.encode(A, $)
			},
			decode : function($, _) {
				this.dom.decode($, _);
				this.text = this.dom.getAttribute("name")
			}
		});
Gef.ns("Gef.jbs.model");
Gef.jbs.model.StateModel = Gef.extend(Gef.model.NodeModel, {
			type : "state",
			encode : function(_, $) {
				var A = "";
				Gef.each(this.outgoingConnections, function(_) {
							A += _.encode("", $ + "  ")
						});
				Gef.model.JpdlUtil.encodeNodePosition(this);
				this.dom.setAttribute("name", this.text);
				return this.dom.encode(A, $)
			},
			decode : function($, _) {
				this.dom.decode($, _);
				this.text = this.dom.getAttribute("name")
			}
		});
Gef.ns("Gef.jbs.model");
Gef.jbs.model.HqlModel = Gef.extend(Gef.model.NodeModel, {
			type : "hql",
			encode : function(_, $) {
				var A = "";
				Gef.each(this.outgoingConnections, function(_) {
							A += _.encode("", $ + "  ")
						});
				Gef.model.JpdlUtil.encodeNodePosition(this);
				this.dom.setAttribute("name", this.text);
				return this.dom.encode(A, $)
			},
			decode : function($, _) {
				this.dom.decode($, _);
				this.text = this.dom.getAttribute("name")
			}
		});
Gef.ns("Gef.jbs.model");
Gef.jbs.model.SqlModel = Gef.extend(Gef.model.NodeModel, {
			type : "sql",
			encode : function(_, $) {
				var A = "";
				Gef.each(this.outgoingConnections, function(_) {
							A += _.encode("", $ + "  ")
						});
				Gef.model.JpdlUtil.encodeNodePosition(this);
				this.dom.setAttribute("name", this.text);
				return this.dom.encode(A, $)
			},
			decode : function($, _) {
				this.dom.decode($, _);
				this.text = this.dom.getAttribute("name")
			}
		});
Gef.ns("Gef.jbs.model");
Gef.jbs.model.DeriveModel = Gef.extend(Gef.model.NodeModel, {
			type : "task",
			encode : function(_, $) {
				var A = "1";
				Gef.each(this.outgoingConnections, function(_) {
							A += _.encode("", $ + "  ")
						});
				Gef.model.JpdlUtil.encodeNodePosition(this);
				this.dom.setAttribute("name", this.text);
				return this.dom.encode(A, $)
			},
			decode : function($, _) {
				this.dom.decode($, _);
				this.text = this.dom.getAttribute("name")
			}
		});
Gef.ns("Gef.jbs.model");
Gef.jbs.model.IdeaModel = Gef.extend(Gef.model.NodeModel, {
			type : "task",
			encode : function(_, $) {
				var A = "";
				Gef.each(this.outgoingConnections, function(_) {
							A += _.encode("", $ + "  ")
						});
				Gef.model.JpdlUtil.encodeNodePosition(this);
				this.dom.setAttribute("name", this.text);
				return this.dom.encode(A, $)
			},
			decode : function($, _) {
				this.dom.decode($, _);
				this.text = this.dom.getAttribute("name")
			}
		});
Gef.ns("Gef.jbs.model");
Gef.jbs.model.MissionModel = Gef.extend(Gef.model.NodeModel, {
			type : "task",
			encode : function(_, $) {
				var A = "";
				Gef.each(this.outgoingConnections, function(_) {
							A += _.encode("", $ + "  ")
						});
				Gef.model.JpdlUtil.encodeNodePosition(this);
				this.dom.setAttribute("name", this.text);
				return this.dom.encode(A, $)
			},
			decode : function($, _) {
				this.dom.decode($, _);
				this.text = this.dom.getAttribute("name")
			}
		});
Gef.ns("Gef.jbs.model");
Gef.jbs.model.JavaModel = Gef.extend(Gef.model.NodeModel, {
			type : "java",
			encode : function(_, $) {
				var A = "";
				Gef.each(this.outgoingConnections, function(_) {
							A += _.encode("", $ + "  ")
						});
				Gef.model.JpdlUtil.encodeNodePosition(this);
				this.dom.setAttribute("name", this.text);
				return this.dom.encode(A, $)
			},
			decode : function($, _) {
				this.dom.decode($, _);
				this.text = this.dom.getAttribute("name")
			}
		});
Gef.ns("Gef.jbs.model");
Gef.jbs.model.ScriptModel = Gef.extend(Gef.model.NodeModel, {
			type : "script",
			encode : function(_, $) {
				var A = "";
				Gef.each(this.outgoingConnections, function(_) {
							A += _.encode("", $ + "  ")
						});
				Gef.model.JpdlUtil.encodeNodePosition(this);
				this.dom.setAttribute("name", this.text);
				return this.dom.encode(A, $)
			},
			decode : function($, _) {
				this.dom.decode($, _);
				this.text = this.dom.getAttribute("name")
			}
		});
Gef.ns("Gef.jbs.model");
Gef.jbs.model.TaskModel = Gef.extend(Gef.model.NodeModel, {
			type : "task",
			encode : function(_, $) {
				var A = "";
				Gef.each(this.outgoingConnections, function(_) {
							A += _.encode("", $ + "  ")
						});
				Gef.model.JpdlUtil.encodeNodePosition(this);
				this.dom.setAttribute("name", this.text);
				return this.dom.encode(A, $)
			},
			decode : function($, _) {
				this.dom.decode($, _);
				this.text = this.dom.getAttribute("name");
				this.flowtype = this.dom.getAttribute("flowtype")  //只在初次加载xml时生效 添加流程类型
			}
		});
Gef.ns("Gef.jbs.model");
Gef.jbs.model.DecisionModel = Gef.extend(Gef.model.NodeModel, {
	type : "decision",
	isValid : function() {
		var C = this.dom.getAttribute("expr");
		if (typeof C != "undefined" && C != null && C != "")
			return true;
		var E = this.dom.getElementAttribute("handler", "class");
		if (typeof E != "undefined" && E != null && E != "")
			return true;
		var A = 0;
		Gef.each(this.getOutgoingConnections(), function($) {
					if (Gef.notEmpty($.dom.getElementAttribute("condition",
							"expr")))
						A++
				});
		var B = this.getOutgoingConnections().length, _ = false;
		//2012-08-21 弹出错误信息。。。 难找啊
		if (A == 0)
			Ext.Msg
					.alert(
							"\u63d0\u793a",
							"["
									+ this.text
									+ "]\u9700\u8981\u4e3a\u8fde\u7ebf\u914d\u7f6e\u6761\u4ef6");
		else if (B - A > 1)
			Ext.Msg
					.alert(
							"\u63d0\u793a",
							"["
									+ this.text
									+ "]\u540e\u53ea\u80fd\u6709\u4e00\u6761\u65e0\u6761\u4ef6\u5916\u5411\u8fde\u7ebf");
		else {
			if (B - A == 1) {
				var D = 0;
				Gef.each(this.getOutgoingConnections(), function(_, $) {
							if (Gef.isEmpty(_.condition)) {
								D = $;
								return false
							}
						});
				var $ = this.getOutgoingConnections().splice(D, 1);
				this.getOutgoingConnections().push($[0])
			}
			_ = true
		}
		return _
	},
	encode : function(_, $) {
		var A = "";
		Gef.each(this.outgoingConnections, function(_) {
					A += _.encode("", $ + "  ")
				});
		Gef.model.JpdlUtil.encodeNodePosition(this);
		this.dom.setAttribute("name", this.text);
		return this.dom.encode(A, $)
	},
	decode : function($, _) {
		this.dom.decode($, _);
		this.text = this.dom.getAttribute("name")
	}
});
Gef.ns("Gef.jbs.model");
Gef.jbs.model.ForkModel = Gef.extend(Gef.model.NodeModel, {
			type : "fork",
			encode : function(_, $) {
				var A = "";
				Gef.each(this.outgoingConnections, function(_) {
							A += _.encode("", $ + "  ")
						});
				Gef.model.JpdlUtil.encodeNodePosition(this);
				this.dom.setAttribute("name", this.text);
				return this.dom.encode(A, $)
			},
			decode : function($, _) {
				this.dom.decode($, _);
				this.text = this.dom.getAttribute("name")
			}
		});
Gef.ns("Gef.jbs.model");
Gef.jbs.model.JoinModel = Gef.extend(Gef.model.NodeModel, {
	type : "join",
	encode : function(_, $) {
		var A = "";
		Gef.each(this.outgoingConnections, function(_) {
					A += _.encode("", $ + "  ")
				});
		Gef.model.JpdlUtil.encodeNodePosition(this);
		this.dom.setAttribute("name", this.text);
		return this.dom.encode(A, $)
	},
	decode : function($, _) {
		this.dom.decode($, _);
		this.text = this.dom.getAttribute("name")
	},
	isValid : function() {
		var $ = this.dom.getAttribute("multiplicity");
		if (typeof $ != "undefined" && $ != "" && $ < 1) {
			Ext.Msg
					.alert(
							"\u4fe1\u606f",
							"["
									+ this.text
									+ "]\u6c47\u805a\u6570\u76ee\u5fc5\u987b\u5927\u4e8e\u96f6");
			return false
		}
		return true
	}
});
Gef.ns("Gef.jbs.model");
Gef.jbs.model.CustomModel = Gef.extend(Gef.model.NodeModel, {
			type : "custom",
			encode : function(_, $) {
				var A = "";
				Gef.each(this.outgoingConnections, function(_) {
							A += _.encode("", $ + "  ")
						});
				Gef.model.JpdlUtil.encodeNodePosition(this);
				this.dom.setAttribute("name", this.text);
				return this.dom.encode(A, $)
			},
			decode : function($, _) {
				this.dom.decode($, _);
				this.text = this.dom.getAttribute("name")
			}
		});
Gef.ns("Gef.jbs.model");
Gef.jbs.model.MailModel = Gef.extend(Gef.model.NodeModel, {
			type : "mail",
			encode : function(_, $) {
				var A = "";
				Gef.each(this.outgoingConnections, function(_) {
							A += _.encode("", $ + "  ")
						});
				Gef.model.JpdlUtil.encodeNodePosition(this);
				this.dom.setAttribute("name", this.text);
				return this.dom.encode(A, $)
			},
			decode : function($, _) {
				this.dom.decode($, _);
				this.text = this.dom.getAttribute("name")
			}
		});
Gef.ns("Gef.jbs.model");
Gef.jbs.model.SubProcessModel = Gef.extend(Gef.model.NodeModel, {
			type : "subProcess",
			getTagName : function() {
				return "sub-process"
			},
			encode : function(_, $) {
				var A = "";
				Gef.each(this.outgoingConnections, function(_) {
							A += _.encode("", $ + "  ")
						});
				Gef.model.JpdlUtil.encodeNodePosition(this);
				this.dom.setAttribute("name", this.text);
				return this.dom.encode(A, $)
			},
			decode : function($, _) {
				this.dom.decode($, _);
				this.text = this.dom.getAttribute("name")
			}
		});
Gef.ns("Gef.jbs.model");
Gef.jbs.model.GroupModel = Gef.extend(Gef.model.NodeModel, {
			type : "group",
			encode : function(_, $) {
				var A = "";
				Gef.each(this.outgoingConnections, function(_) {
							A += _.encode("", $ + "  ")
						});
				Gef.model.JpdlUtil.encodeNodePosition(this);
				this.dom.setAttribute("name", this.text);
				return this.dom.encode(A, $)
			},
			decode : function($, _) {
				this.dom.decode($, _);
				this.text = this.dom.getAttribute("name")
			}
		});
Gef.ns("Gef.jbs.model");
Gef.jbs.model.TransitionModel = Gef.extend(Gef.model.ConnectionModel, {
			type : "transition",
			encode : function(_, $) {
				if(this.text && this.text.indexOf("end")>0) this.text='同意';
				if(this.text && this.text.indexOf('cancel')>0) this.text='不同意';
				this.dom.setAttribute("name", this.text);
				this.dom.setAttribute("g", Gef.model.JpdlUtil
								.encodeConnectionPosition(this));
				this.dom.setAttribute("to", this.target.text);
				return this.dom.encode("", $)
			},
			decode : function($, A) {
				this.dom.decode($, A);
				this.text = this.dom.getAttribute("name");
				var _ = this.dom.getElementAttribute("condition", "expr")
			}
		});
Gef.ns("Gef.jbs.model");
Gef.jbs.model.JmsModel = Gef.extend(Gef.model.NodeModel, {
			type : "jms",
			encode : function(_, $) {
				var A = "";
				Gef.each(this.outgoingConnections, function(_) {
							A += _.encode("", $ + "  ")
						});
				Gef.model.JpdlUtil.encodeNodePosition(this);
				this.dom.setAttribute("name", this.text);
				return this.dom.encode(A, $)
			},
			decode : function($, _) {
				this.dom.decode($, _);
				this.text = this.dom.getAttribute("name")
			}
		});
Gef.ns("Gef.jbs.model");
Gef.jbs.model.RuleDecisionModel = Gef.extend(Gef.model.NodeModel, {
			type : "ruleDecision",
			getTagName : function() {
				return "rule-decision"
			},
			encode : function(_, $) {
				var A = "";
				Gef.each(this.outgoingConnections, function(_) {
							A += _.encode("", $ + "  ")
						});
				Gef.model.JpdlUtil.encodeNodePosition(this);
				this.dom.setAttribute("name", this.text);
				return this.dom.encode(A, $)
			},
			decode : function($, _) {
				this.dom.decode($, _);
				this.text = this.dom.getAttribute("name")
			}
		});
Gef.ns("Gef.jbs.model");
Gef.jbs.model.RulesModel = Gef.extend(Gef.model.NodeModel, {
			type : "rules",
			encode : function(_, $) {
				var A = "";
				Gef.each(this.outgoingConnections, function(_) {
							A += _.encode("", $ + "  ")
						});
				Gef.model.JpdlUtil.encodeNodePosition(this);
				this.dom.setAttribute("name", this.text);
				return this.dom.encode(A, $)
			},
			decode : function($, _) {
				this.dom.decode($, _);
				this.text = this.dom.getAttribute("name")
			}
		});
Gef.ns("Gef.jbs.model");
Gef.jbs.model.AutoModel = Gef.extend(Gef.model.NodeModel, {
			type : "auto",
			encode : function(_, $) {
				var A = "";
				Gef.each(this.outgoingConnections, function(_) {
							A += _.encode("", $ + "  ")
						});
				Gef.model.JpdlUtil.encodeNodePosition(this);
				this.dom.setAttribute("name", this.text);
				return this.dom.encode(A, $)
			},
			decode : function($, _) {
				this.dom.decode($, _);
				this.text = this.dom.getAttribute("name")
			}
		});
Gef.ns("Gef.jbs.model");
Gef.jbs.model.HumanModel = Gef.extend(Gef.model.NodeModel, {
			type : "human",
			encode : function(_, $) {
				var A = "";
				Gef.each(this.outgoingConnections, function(_) {
							A += _.encode("", $ + "  ")
						});
				Gef.model.JpdlUtil.encodeNodePosition(this);
				this.dom.setAttribute("name", this.text);
				return this.dom.encode(A, $)
			},
			decode : function($, _) {
				this.dom.decode($, _);
				this.text = this.dom.getAttribute("name")
			}
		});
Gef.ns("Gef.jbs.model");
Gef.jbs.model.CounterSignModel = Gef.extend(Gef.model.NodeModel, {
			type : "counter-sign",
			encode : function(_, $) {
				var A = "";
				Gef.each(this.outgoingConnections, function(_) {
							A += _.encode("", $ + "  ")
						});
				Gef.model.JpdlUtil.encodeNodePosition(this);
				this.dom.setAttribute("name", this.text);
				return this.dom.encode(A, $)
			},
			decode : function($, _) {
				this.dom.decode($, _);
				this.text = this.dom.getAttribute("name")
			}
		});
Gef.ns("Gef.jbs.model");
Gef.jbs.model.ForeachModel = Gef.extend(Gef.model.NodeModel, {
			type : "foreach",
			encode : function(_, $) {
				var A = "";
				Gef.each(this.outgoingConnections, function(_) {
							A += _.encode("", $ + "  ")
						});
				Gef.model.JpdlUtil.encodeNodePosition(this);
				this.dom.setAttribute("name", this.text);
				return this.dom.encode(A, $)
			},
			decode : function($, _) {
				this.dom.decode($, _);
				this.text = this.dom.getAttribute("name")
			}
		});
Gef.ns("Gef.jbs.figure");
Gef.jbs.figure.AbstractNodeFigure = Gef.extend(Gef.figure.NodeFigure, {
			getTools : function() {
				//节点使用tool
				if (!this.tools)
					this.tools = [new Gef.jbs.tool.SetTool()];
				if(_ReadOnly){
					this.tools = [];
				}
				if(this.flowtype&&this.flowtype != "task"){
					this.tools = [];
				}
				if(this.name.indexOf('derive')>=0||
					this.name.indexOf('mission')>=0||
					this.name.indexOf('idea')>=0){
					this.tools = [];
				}
				return this.tools
			}
		});
Gef.ns("Gef.jbs.figure");
Gef.jbs.figure.AbstractImageFigure = Gef.extend(Gef.figure.ImageNodeFigure, {
	getTools : function() {
		if (!this.tools)
			//决策节点使用设置按钮
			this.tools = [new Gef.jbs.tool.SetTool()];
		if(_ReadOnly){
			this.tools = []
		}
		return this.tools
	}
});
Gef.ns("Gef.jbs.figure");
Gef.jbs.figure.AbstractEndImageFigure = Gef.extend(Gef.figure.ImageNodeFigure,
		{
			getTools : function() {
				if (!this.tools)
					//END节点使用设置按钮
					this.tools = [new Gef.jbs.tool.SetTool()];
				if(_ReadOnly){
					this.tools = []
				}
				return this.tools
			}
		});
Gef.ns("Gef.jbs.figure");
Gef.jbs.figure.AbstractStartImageFigure = Gef.extend(
		Gef.figure.ImageNodeFigure, {
			getTools : function() {
				if (!this.tools)
					//START节点不使用设置按钮
					this.tools = [/*new Gef.jbs.tool.SetTool()*/];
				if(_ReadOnly){
					this.tools = []
				}
				return this.tools
			}
		});
Gef.ns("Gef.jbs.figure");
Gef.jbs.figure.ProcessFigure = Gef.extend(Gef.figure.NoFigure, {});
Gef.ns("Gef.jbs.figure");
Gef.jbs.figure.StartFigure = Gef.extend(
		Gef.jbs.figure.AbstractStartImageFigure, {
			constructor : function($) {
				Gef.jbs.figure.StartFigure.superclass.constructor.call(this, $);
				this.url = Gef.IMAGE_ROOT + "start_event_empty.png"
			}
		});
Gef.ns("Gef.jbs.figure");
Gef.jbs.figure.EndFigure = Gef.extend(Gef.jbs.figure.AbstractEndImageFigure, {
			constructor : function($) {
				Gef.jbs.figure.EndFigure.superclass.constructor.call(this, $);
				this.url = Gef.IMAGE_ROOT + "end_event_terminate.png"
			}
		});
Gef.ns("Gef.jbs.figure");
Gef.jbs.figure.CancelFigure = Gef.extend(Gef.jbs.figure.AbstractEndImageFigure,
		{
			constructor : function($) {
				Gef.jbs.figure.CancelFigure.superclass.constructor
						.call(this, $);
				this.url = Gef.IMAGE_ROOT + "end_event_cancel.png"
			}
		});
Gef.ns("Gef.jbs.figure");
Gef.jbs.figure.ErrorFigure = Gef.extend(Gef.jbs.figure.AbstractEndImageFigure,
		{
			constructor : function($) {
				Gef.jbs.figure.ErrorFigure.superclass.constructor.call(this, $);
				this.url = Gef.IMAGE_ROOT + "end_event_error.png"
			}
		});
Gef.ns("Gef.jbs.figure");
Gef.jbs.figure.StateFigure = Gef.extend(Gef.jbs.figure.AbstractNodeFigure, {});
Gef.ns("Gef.jbs.figure");
Gef.jbs.figure.HqlFigure = Gef.extend(Gef.jbs.figure.AbstractNodeFigure, {});
Gef.ns("Gef.jbs.figure");
Gef.jbs.figure.SqlFigure = Gef.extend(Gef.jbs.figure.AbstractNodeFigure, {});
Gef.ns("Gef.jbs.figure");
Gef.jbs.figure.MissionFigure = Gef.extend(Gef.jbs.figure.AbstractNodeFigure, {});
Gef.ns("Gef.jbs.figure");
Gef.jbs.figure.IdeaFigure = Gef.extend(Gef.jbs.figure.AbstractNodeFigure, {});
Gef.ns("Gef.jbs.figure");
Gef.jbs.figure.JavaFigure = Gef.extend(Gef.jbs.figure.AbstractNodeFigure, {});
Gef.ns("Gef.jbs.figure");
Gef.jbs.figure.ScriptFigure = Gef.extend(Gef.jbs.figure.AbstractNodeFigure, {});
Gef.ns("Gef.jbs.figure");
Gef.jbs.figure.TaskFigure = Gef.extend(Gef.jbs.figure.AbstractNodeFigure, {});
Gef.ns("Gef.jbs.figure");
Gef.jbs.figure.DecisionFigure = Gef.extend(Gef.jbs.figure.AbstractImageFigure,
		{
			constructor : function($) {
				Gef.jbs.figure.DecisionFigure.superclass.constructor.call(this,
						$);
				this.url = Gef.IMAGE_ROOT + "gateway_exclusive.png"
			}
		});
Gef.ns("Gef.jbs.figure");
Gef.jbs.figure.ForkFigure = Gef.extend(Gef.jbs.figure.AbstractImageFigure, {
			constructor : function($) {
				Gef.jbs.figure.ForkFigure.superclass.constructor.call(this, $);
				this.url = Gef.IMAGE_ROOT + "gateway_parallel.png"
			}
		});
Gef.ns("Gef.jbs.figure");
Gef.jbs.figure.JoinFigure = Gef.extend(Gef.jbs.figure.AbstractImageFigure, {
			constructor : function($) {
				Gef.jbs.figure.JoinFigure.superclass.constructor.call(this, $);
				this.url = Gef.IMAGE_ROOT + "gateway_parallel.png"
			}
		});
Gef.ns("Gef.jbs.figure");
Gef.jbs.figure.CustomFigure = Gef.extend(Gef.jbs.figure.AbstractNodeFigure, {});
Gef.ns("Gef.jbs.figure");
Gef.jbs.figure.MailFigure = Gef.extend(Gef.jbs.figure.AbstractNodeFigure, {});
Gef.ns("Gef.jbs.figure");
Gef.jbs.figure.SubProcessFigure = Gef.extend(Gef.jbs.figure.AbstractNodeFigure,
		{});
Gef.ns("Gef.jbs.figure");
Gef.jbs.figure.GroupNodeFigure = Gef.extend(Gef.figure.NodeFigure, {});
Gef.ns("Gef.jbs.figure");
Gef.jbs.figure.TransitionFigure = Gef.extend(Gef.figure.EdgeFigure, {});
Gef.ns("Gef.jbs.figure");
Gef.jbs.figure.JmsFigure = Gef.extend(Gef.jbs.figure.AbstractNodeFigure, {});
Gef.ns("Gef.jbs.figure");
Gef.jbs.figure.RuleDecisionFigure = Gef.extend(
		Gef.jbs.figure.AbstractImageFigure, {
			constructor : function($) {
				Gef.jbs.figure.RuleDecisionFigure.superclass.constructor.call(
						this, $);
				this.url = Gef.IMAGE_ROOT + "gateway_exclusive.png"
			}
		});
Gef.ns("Gef.jbs.figure");
Gef.jbs.figure.RulesFigure = Gef.extend(Gef.jbs.figure.AbstractNodeFigure, {});
Gef.ns("Gef.jbs.figure");
Gef.jbs.figure.AutoFigure = Gef.extend(Gef.jbs.figure.AbstractNodeFigure, {});
Gef.ns("Gef.jbs.figure");
Gef.jbs.figure.HumanFigure = Gef.extend(Gef.jbs.figure.AbstractNodeFigure, {});
Gef.ns("Gef.jbs.figure");
Gef.jbs.figure.CounterSignFigure = Gef.extend(
		Gef.jbs.figure.AbstractNodeFigure, {});
Gef.ns("Gef.jbs.figure");
Gef.jbs.figure.ForeachFigure = Gef.extend(Gef.jbs.figure.AbstractImageFigure, {
			constructor : function($) {
				Gef.jbs.figure.ForeachFigure.superclass.constructor.call(this,
						$);
				this.url = Gef.IMAGE_ROOT + "gateway_parallel.png"
			}
		});
Gef.ns("Gef.jbs.tool");
Gef.jbs.tool.ChangeTypeTool = Gef.extend(Gef.tool.AbstractImageTool, {
	getKey : function() {
		return "changeTypeTool"
	},
	needCheckOutgo : function() {
		return false
	},
	getUrl : function() {
		return Gef.IMAGE_ROOT + "../../tools/wrench_orange.png"
	},
	getX : function($) {
		return 5
	},
	getY : function($) {
		return $ + 5
	},
	getConnectionModelName : function() {
		return "connection"
	},
	drag : function($, _) {
		var A = document.createElement("div");
		A.style.position = "absolute";
		A.style.left = _.point.absoluteX + "px";
		A.style.top = _.point.absoluteY + "px";
		A.style.backgroundColor = "#DDEEDD";
		Gef.each(this.allowedTypes, function(_) {
					if (_.type == this.node.editPart.model.getType())
						return true;
					var $ = document.createElement("div");
					$.onmouseover = function() {
						this.style.backgroundColor = "yellow"
					};
					$.onmouseout = function() {
						this.style.backgroundColor = ""
					};
					$.style.cursor = "pointer";
					$.className = "_gef_changeType";
					$.setAttribute("title", _.type);
					$.innerHTML = _.name;
					A.appendChild($)
				}, this);
		document.body.appendChild(A);
		$.changeTypeDiv = A
	},
	move : function($, _) {
	},
	drop : function(B, C) {
		var A = C.target;
		if (A.className == "_gef_changeType") {
			var E = A.getAttribute("title"), D = this.node.editPart.model, _ = B
					.getModelFactory().createModel(E), $ = new Gef.commands.CompoundCommand();
			$.addCommand(new Gef.gef.command.CreateNodeCommand(_,
					D.getParent(), {
						x : D.x,
						y : D.y,
						w : D.w,
						h : D.h
					}));
			Gef.each(D.getIncomingConnections(), function(A) {
						var D = A.getType(), C = B.getModelFactory()
								.createModel(D);
						C.text = A.text;
						$
								.addCommand(new Gef.gef.command.RemoveConnectionCommand(A));
						$
								.addCommand(new Gef.gef.command.CreateConnectionCommand(
										C, A.getSource(), _));
						$
								.addCommand(new Gef.gef.command.ResizeConnectionCommand(
										C, [], A.innerPoints))
					});
			Gef.each(D.getOutgoingConnections(), function(A) {
						var D = A.getType(), C = B.getModelFactory()
								.createModel(D);
						C.text = A.text;
						$
								.addCommand(new Gef.gef.command.RemoveConnectionCommand(A));
						$
								.addCommand(new Gef.gef.command.CreateConnectionCommand(
										C, _, A.getTarget()));
						$
								.addCommand(new Gef.gef.command.ResizeConnectionCommand(
										C, [], A.innerPoints))
					});
			$.addCommand(new Gef.gef.command.RemoveNodeCommand(D));
			B.getCommandStack().execute($);
			B.getSelectionManager().addSelectedNode(_.editPart)
		}
		document.body.removeChild(B.changeTypeDiv)
	}
});
Gef.ns("Gef.jbs.tool");
Gef.jbs.tool.TaskTool = Gef.extend(Gef.tool.AbstractImageTool, {
			getKey : function() {
				return "taskTool"
			},
			getUrl : function() {
				return Gef.IMAGE_ROOT + "../../tools/new_task.png"
			},
			getNodeConfig : function() {
				return {
					text : "human",
					w : 90,
					h : 50
				}
			},
			getY : function() {
				return 0
			},
			getConnectionModelName : function() {
				return "transition"
			}
		});
Gef.ns("Gef.jbs.tool");
//线条tool hey
Gef.jbs.tool.SetLineTool = Gef.extend(Gef.tool.AbstractImageTool, {
			getKey : function() {
				return "SetLineTool"
			},
			getUrl : function() {
				return Gef.IMAGE_ROOT + "../../tools/lineset.png"
			},
			getY : function() {
				return 30
			},
			getConnectionModelName : function() {
				return "transition"
			},
			//点击 hey
			isClickedSvg:function(A){
				var _ = A.target, $ = _.getAttribute("id");
				if (!$)
					return false;
				if (_.tagName == "text"){
						var from = this.node.from;
						var fromTagName = from.editPart.model.dom.tagName;
						var fromType = from.editPart.model.flowtype;
						var to = this.node.to;
						var toTagName = to.editPart.model.dom.tagName;
						var toType = to.editPart.model.flowtype;
						var model = this.node.editPart.model;
						if(from.name=="START"&&toType=='task'){//提交操作
							model.dom.setAttribute('linetype', 'Commit');
							model.text = '提交';
						 	model.editPart.figure.updateAndShowText('提交');
						}else if(fromTagName=='task'&&(toType=='task'||to.name=='END'||toTagName=='decision')){//普通操作
							model.dom.setAttribute('linetype', 'Turn');
						}else if(fromTagName=='task'&&toType=='derive'){//派生流程操作
							model.dom.setAttribute('linetype', 'Flow');
						}else if(fromTagName=='task'&&toType=='mission'){//派生任务操作
							model.dom.setAttribute('linetype', 'Task');
						}else if(fromTagName=='task'&&toType=='idea'){//派生意见操作
							model.dom.setAttribute('linetype', 'Update');
						}else if(toType=='ideal'){//决策操作
							model.dom.setAttribute('linetype', 'Update');
						}else if(fromTagName=='decision'){//决策操作
							model.dom.setAttribute('linetype', 'Judge');
						}
				}
				if (_.tagName == "image" && $.indexOf('SetLineTool')>=0){
						var type = this.node.editPart.model.linetype;
						if(!type){
							Ext.Msg.alert('提示', '请选择操作属性再编辑操作');
							return false;
						}
						var name = this.node.editPart.model.text;
						var formNode = this.node.editPart.model.source;	
						var toNode = this.node.editPart.model.target;	
						if(formNode.flowtype=='derive'){
							Ext.Msg.alert('提示', '该操作自动生成，请编辑来源派生流程');
							return false;
						}else{
							Ext.Ajax.request({
							    url: basePath + 'oa/flow/checkNodeSaved.action',
							    params: {
							        shortName: shortName,
							        fromNodeName:formNode.text,
							        toNodeName:toNode.text,
							        operationName:name,
							        operationType:type
							    },
							    success: function(response){
							    	var data = new Ext.decode(response.responseText);
							    	var fromId = data.data.fromId;
							    	var toId = data.data.toId;
							    	var fromNodeName = data.data.fromNodeName;
							    	var toNodeName = data.data.toNodeName;
							    	var name = data.operationName;
							    	var type = data.operationType;
									var win = new Ext.Window({
										title: '<span style="color:#115fd8;">操作编辑</span>',
										draggable:true,
										height: window.innerHeight*0.95,
										width: '90%',
										resizable:false,
										id:'operationEdit',
								   		modal: true,
								   		layout:'fit',
								   		items:[{
											xtype:'panel',
											tag : 'iframe',
											frame : true,
											border : false,
											html : '<iframe id="editPage" src="'+basePath+'jsps/oa/flow/FlowEditor.jsp?fd_id='+fd_id+'&type='+type+'&name='+name+'&caller='+caller+'&fromId='+fromId+'&toId='+toId+'&fromNodeName='+fromNodeName+'&toNodeName='+toNodeName+'&shortName='+shortName+'" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
								   		}]
									});
									win.show();
									return false;
							    },
							    failure: function(response) {
							   		Ext.Msg.alert('错误提示', response.responseText);
							   	}
							});
							return false
						}
						return false
				}
			}
		});
Gef.ns("Gef.jbs.tool");
//节点tool hey
Gef.jbs.tool.SetTool = Gef.extend(Gef.tool.AbstractImageTool, {
			getKey : function() {
				return "SetTool"
			},
			getUrl : function() {
				return Gef.IMAGE_ROOT + "../../tools/set.png"
			},
			getY : function() {
				return 30
			},
			getConnectionModelName : function() {
				return "transition"
			},
			//点击 hey
			isClickedSvg:function(A){
				var _ = A.target, $ = _.getAttribute("id");
				if (!$)
					return false;
				if (_.tagName == "image"){
					if($.indexOf('SetTool')>=0){
						var type = this.node.editPart.model.flowtype;
						if(!type){
							if(this.node.editPart.model.getTagName()=='end'){
								type = 'task'
							}
							if(this.node.editPart.model.getTagName()=='decision'){
								type = 'judge'
							}
						}
						var name = this.node.editPart.model.text;
						var win = new Ext.Window({
							title: '<span style="color:#115fd8;">节点编辑</span>',
							draggable:true,
							height: window.innerHeight*0.95,
							width: '90%',
							resizable:false,
							id:'nodeEdit',
					   		modal: true,
					   		layout:'fit',
					   		items:[{
								xtype:'panel',
								tag : 'iframe',
								frame : true,
								border : false,
								html : '<iframe id="editPage" src="'+basePath+'jsps/oa/flow/FlowEditor.jsp?fd_id='+fd_id+'&type='+type+'&name='+name+'&caller='+caller+'&shortName='+shortName+'" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
					   		}]
						});
						win.show();
					}
					return false
				}
			}
		});
Gef.ns("Gef.jbs.tool");
Gef.jbs.tool.EndTool = Gef.extend(Gef.tool.AbstractImageTool, {
			getKey : function() {
				return "endTool"
			},
			getUrl : function() {
				return Gef.IMAGE_ROOT + "../../tools/new_event.png"
			},
			getNodeConfig : function() {
				return {
					text : "end",
					w : 48,
					h : 48
				}
			},
			getY : function() {
				return 20
			},
			getConnectionModelName : function() {
				return "transition"
			}
		});
Gef.ns("Gef.jbs.tool");
Gef.jbs.tool.GatewayTool = Gef.extend(Gef.tool.AbstractImageTool, {
			getKey : function() {
				return "gatewayTool"
			},
			getUrl : function() {
				return Gef.IMAGE_ROOT + "../../tools/new_gateway_xor_data.png"
			},
			getNodeConfig : function() {
				return {
					text : "decision",
					w : 48,
					h : 48
				}
			},
			getY : function() {
				return 40
			},
			getConnectionModelName : function() {
				return "transition"
			}
		});
Gef.ns("Gef.jbs.tool");
Gef.jbs.tool.LineTool = Gef.extend(Gef.tool.AbstractEdgeTool, {
			getKey : function() {
				return "lineTool"
			},
			getY : function() {
				return 60
			},
			getConnectionModelName : function() {
				return "transition"
			}
		});
Gef.ns("Gef.jbs.xml");
Gef.jbs.xml.JBSSerializer = Gef.extend(Gef.gef.xml.XmlSerializer, {});
Gef.ns("Gef.jbs.xml");
Gef.jbs.xml.JBSDeserializer = Gef.extend(Gef.gef.xml.XmlDeserializer, {
	decode : function() {
		this.modelMap = {};
		this.domMap = {};
		var $ = new Gef.jbs.model.ProcessModel();
		this.parseRoot($);
		delete this.modelMap;
		delete this.domMap;
		return $
	},
	parseRoot : function(_) {
		var $ = this.xdoc.documentElement;
		_.decode($, ["auto", "cancel", "counter-sign", "custom",
						"decision", "end", "error", "fork", "group", "hql",
						"human", "java", "jms", "join", "mail",
						"rule-decision", "rules", "script", "sql","derive","mission","idea", "start",
						"state", "sub-process", "task", "foreach"]);
		Gef.each($.childNodes, function($) {
					this.parseNodes($, _)
				}, this);
		Gef.each(_.getChildren(), function($) {
					this.parseConnections($)
				}, this)
	},
	parseNodes : function(nodeDom, rootModel) {
		var nodeName = nodeDom.nodeName, nodeModel = Gef.jbs.JBSModelFactory._modelLib[nodeName]
				? eval("new " + Gef.jbs.JBSModelFactory._modelLib[nodeName]
						+ "()")
				: null;
		nodeModel && this.decodeNodeModel(nodeModel, nodeDom, rootModel)
	},
	parseConnections : function($) {
		var _ = this.domMap[$.text];
		Gef.each(_.childNodes, function(_) {
					if (_.nodeName == "transition")
						this.parseConnection(_, $)
				}, this)
	},
	parseConnection : function(A, _) {
		var B = new Gef.jbs.model.TransitionModel();
		B.decode(A);
		var $ = A.getAttribute("to"), C = this.modelMap[$];
		if (!C) {
			Gef.error("cannot find targetModel for sourceModel[" + _.text
							+ "], to[" + $ + "]",
					"JBSDeserializer.parseConnection()");
			return
		}
		B.setSource(_);
		_.addOutgoingConnection(B);
		B.setTarget(C);
		C.addIncomingConnection(B);
		Gef.model.JpdlUtil.decodeConnectionPosition(B)
	}
});
Gef.jbs.model.ShapeBaseModel = Gef.extend(Gef.model.NodeModel, {
			fill : "",
			stroke : "black",
			strokewide : 1,
			isValid : function() {
				return true
			},
			encode : function(_, $) {
				var A = "";
				Gef.each(this.outgoingConnections, function(_) {
							A += _.encode("", $ + "  ")
						});
				Gef.model.JpdlUtil.encodeNodePosition(this);
				this.dom.setAttribute("name", this.text);
				return this.dom.encode(A, $)
			},
			decode : function($, _) {
				this.dom.decode($, _);
				this.text = this.dom.getAttribute("name");
				this.fill = this.dom.getAttribute("fill") || "";
				this.stroke = this.dom.getAttribute("stroke") || "black";
				this.strokewide = this.dom.getAttribute("strokewide") || 1
			}
		});
Gef.jbs.figure.ShapeBaseFigure = Gef.extend(Gef.figure.RectFigure, {
			constructor : function($) {
				Gef.jbs.figure.ShapeBaseFigure.superclass.constructor.call(
						this, $);
				this.w = $.w;
				this.h = $.h;
				this.fill = $.fill;
				this.stroke = $.stroke;
				this.strokewide = $.strokewide;
				this.outputs = [];
				this.incomes = []
			},
			renderVml : function() {
			},
			renderVml0 : function() {
			},
			renderSvg : function() {
			},
			renderSvg0 : function($) {
			},
			moveToVml : function() {
				this.renderVml0(this.el)
			},
			moveToSvg : function(_, $) {
				this.renderSvg0(this.el)
			},
			updateVml : function() {
				this.renderVml0(this.el)
			},
			updateSvg : function() {
				this.renderSvg0(this.el)
			},
			getTools : function() {
				return []
			}
		});
Gef.jbs.editpart.GenericEditPart = Gef.extend(Gef.gef.editparts.NodeEditPart, {
			_figureClassName : "Exception",
			_getFigureParam : function() {
				return ["x", "y", "w", "h"]
			},
			createFigure : function() {
				var model = this.getModel(), p = {}, pk = this
						._getFigureParam();
				for (var i = 0; i < pk.length; i++)
					p[pk[i]] = this.model[pk[i]];
				p["name"] = this.model[pk["text"]];
				var figure = eval("new " + this._figureClassName + "(p)");
				figure.editPart = this;
				return figure
			},
			canResize : function() {
				return true
			}
		});
Gef.jbs.editpart.ShapeBaseEditPart = Gef.extend(
		Gef.jbs.editpart.GenericEditPart, {
			_figureClassName : "Gef.jbs.figure.ShapeBaseFigure",
			_getFigureParam : function() {
				return ["x", "y", "w", "h", "fill", "stroke", "strokewide"]
			}
		});
Gef.jbs.model.GenericImageModel = Gef.extend(Gef.model.NodeModel, {
			type : "image",
			isValid : function() {
				return true
			},
			encode : function(_, $) {
				var A = "";
				Gef.each(this.outgoingConnections, function(_) {
							A += _.encode("", $ + "  ")
						});
				Gef.model.JpdlUtil.encodeNodePosition(this);
				this.dom.setAttribute("name", this.text);
				this.dom.setAttribute("url", this.text);
				return this.dom.encode(A, $)
			},
			decode : function($, _) {
				this.dom.decode($, _);
				this.text = this.dom.getAttribute("name");
				this.url = this.dom.getAttribute("url") || this.url
			}
		});
Gef.jbs.figure.GenericImageFigure = Gef.extend(Gef.figure.ImageNodeFigure, {
			constructor : function($) {
				Gef.jbs.figure.GenericImageFigure.superclass.constructor.call(
						this, $);
				this.w = $.w;
				this.h = $.h;
				this.url = $.url
			},
			update : function(B, A, $, _) {
				this.x = B;
				this.y = A;
				this.w = $;
				this.h = _;
				if (Gef.isVml)
					this.updateVml();
				else
					this.updateSvg()
			}
		});
Gef.jbs.editpart.GenericImageEditPart = Gef.extend(
		Gef.jbs.editpart.GenericEditPart, {
			_figureClassName : "Gef.jbs.figure.GenericImageFigure",
			_getFigureParam : function() {
				return ["x", "y", "w", "h", "url"]
			}
		});
Gef.jbs.JBSModelFactory.registerModel("image",
		"Gef.jbs.model.GenericImageModel");
Gef.jbs.JBSEditPartFactory.registerEditPart("image",
		"Gef.jbs.editpart.GenericImageEditPart");
Gef.jbs.model.EllipseModel = Gef.extend(Gef.jbs.model.ShapeBaseModel, {
			type : "ellipse"
		});
Gef.jbs.figure.EllipseFigure = Gef.extend(Gef.jbs.figure.ShapeBaseFigure, {
			renderSvg : function() {
				var $ = document.createElementNS(Gef.svgns, "ellipse");
				this.renderSvg0($);
				this.el = $
			},
			renderSvg0 : function($) {
				$.setAttribute("cx", this.x + this.w / 2 + "px");
				$.setAttribute("cy", this.y + this.h / 2 + "px");
				$.setAttribute("rx", this.w / 2 + "px");
				$.setAttribute("ry", this.h / 2 + "px");
				$.setAttribute("fill", this.fill ? this.fill : "none");
				$.setAttribute("stroke", this.stroke);
				$.setAttribute("stroke-width", this.strokewidth)
			},
			renderVml : function() {
				var $ = document.createElement("v:oval");
				this.renderVml0($);
				this.el = $
			},
			renderVml0 : function($) {
				$.style.left = this.x + "px";
				$.style.top = this.y + "px";
				$.style.width = this.w + "px";
				$.style.height = this.h + "px";
				if (this.fill)
					$.setAttribute("fillcolor", this.fill);
				else
					$.setAttribute("filled", "false");
				$.setAttribute("strokecolor", this.stroke)
			}
		});
Gef.jbs.editpart.EllipseEditPart = Gef.extend(
		Gef.jbs.editpart.ShapeBaseEditPart, {
			_figureClassName : "Gef.jbs.figure.EllipseFigure"
		});
Gef.jbs.JBSModelFactory.registerModel("ellipse", "Gef.jbs.model.EllipseModel");
Gef.jbs.JBSEditPartFactory.registerEditPart("ellipse",
		"Gef.jbs.editpart.EllipseEditPart");
Gef.jbs.model.RectModel = Gef.extend(Gef.jbs.model.ShapeBaseModel, {
			type : "rect",
			rounded : 0,
			encode : function(_, $) {
				this.dom.setAttribute("rounded", this.rounded);
				return Gef.jbs.model.RectModel.superclass.encode.call(this, _,
						$)
			},
			decode : function($, _) {
				Gef.jbs.model.RectModel.superclass.decode.call(this, $, _);
				this.rounded = this.dom.getAttribute("rounded") || 0
			}
		});
Gef.jbs.figure.RectFigure = Gef.extend(Gef.jbs.figure.ShapeBaseFigure, {
			constructor : function($) {
				Gef.jbs.figure.RectFigure.superclass.constructor.call(this, $);
				this.rounded = $.rounded
			},
			renderSvg : function() {
				var $ = document.createElementNS(Gef.svgns, "rect");
				this.renderSvg0($);
				this.el = $
			},
			renderSvg0 : function($) {
				$.setAttribute("x", this.x + "px");
				$.setAttribute("y", this.y + "px");
				$.setAttribute("width", this.w + "px");
				$.setAttribute("height", this.h + "px");
				$.setAttribute("rx", this.rounded);
				$.setAttribute("ry", this.rounded);
				$.setAttribute("fill", this.fill ? this.fill : "none");
				$.setAttribute("stroke", this.stroke);
				$.setAttribute("stroke-width", this.strokewidth)
			},
			renderVml : function() {
				var $ = document.createElement("v:rect");
				this.renderVml0($);
				this.el = $
			},
			renderVml0 : function($) {
				$.style.left = this.x + "px";
				$.style.top = this.y + "px";
				$.style.width = this.w + "px";
				$.style.height = this.h + "px";
				if (this.fill)
					$.fillcolor = this.fill;
				else
					$.filled = "false";
				$.strokecolor = this.stroke;
				$.strokeweight = this.strokewidth + "px"
			}
		});
Gef.jbs.editpart.RectEditPart = Gef.extend(Gef.jbs.editpart.ShapeBaseEditPart,
		{
			_figureClassName : "Gef.jbs.figure.RectFigure",
			_getFigureParam : function() {
				return ["x", "y", "w", "h", "fill", "stroke", "strokewide",
						"rounded"]
			}
		});
Gef.jbs.JBSModelFactory.registerModel("rect", "Gef.jbs.model.RectModel");
Gef.jbs.JBSEditPartFactory.registerEditPart("rect",
		"Gef.jbs.editpart.RectEditPart");
JobExecutor = function($) {
	this.replay = $;
	this.running = false
};
JobExecutor.prototype = {
	start : function() {
		if (this.running !== true) {
			this.running = true;
			this.tid = new Date().getTime();
			this.run(this.tid)
		}
	},
	run : function(C) {
		if (this.running !== true)
			return;
		if (C != this.tid)
			return;
		var $ = 0, A = Array.prototype.slice.call(this.replay.tokens, 0);
		for (var D = 0; D < A.length; D++) {
			var _ = A[D];
			if (_.status === "running") {
				$++;
				_.move()
			}
		}
		if ($ !== 0) {
			var B = this;
			setTimeout(function() {
						B.run(C)
					}, 100)
		} else {
			this.running = false;
			A = [];
			for (D = 0; D < this.replay.tokens.length; D++) {
				_ = this.replay.tokens[D];
				if (_.status !== "removed")
					A.push(_)
			}
			this.replay.tokens = A
		}
	}
};
Node = function(A, $) {
	this.name = A.name;
	this.type = A.type;
	this.x = A.x;
	this.y = A.y;
	if (this.type === "start" || this.type === "end"
			|| this.type === "error" || this.type === "cancel"
			|| this.type === "decision" || this.type === "fork"
			|| this.type === "join") {
		this.w = 48;
		this.h = 48
	} else {
		this.w = A.w;
		this.h = A.h
	}
	this.activity = A;
	this.replay = $;
	this.parent = [];
	this.children = [];
	var _ = this.replay.map[this.name];
	if (typeof _ !== "undefined") {
		if (_ !== this)
			throw new Error("node duplicated, name: " + this.name)
	} else
		this.replay.map[this.name] = this;
	if (!this.isCurrentActivity(this.name))
		this.init()
};
Node.prototype = {
	init : function() {
		if (!this.hasHistory())
			this.findTransitions()
	},
	createChildNode : function(B) {
		var A = B.name, $ = this.replay.map[B.name], _ = null;
		if (typeof $ !== "undefined")
			_ = $;
		else
			_ = new Node(B, this.replay);
		this.children.push(_);
		_.parent.push(this)
	},
	hasHistory : function() {
		var B = this.replay.historyActivities;
		for (var F = 0; F < B.length; F++) {
			var E = B[F];
			if (E.name === this.activity.name) {
				var A = E.t, _ = this.activity.ts;
				for (var C = 0; C < _.length; C++) {
					var $ = _[C];
					if ($.name === A) {
						var D = this.findActivity($.to);
						this.createChildNode(D);
						return true
					}
				}
			}
		}
		return false
	},
	findTransitions : function() {
		var _ = this.activity.ts;
		for (var C = 0; C < _.length; C++) {
			var $ = _[C], A = $.to, B = this.findActivity(A);
			this.createChildNode(B)
		}
	},
	findActivity : function(_) {
		var $ = this.replay.processDefinition;
		for (var B = 0; B < $.length; B++) {
			var A = $[B];
			if (A.name === _)
				return A
		}
	},
	isCurrentActivity : function(_) {
		var $ = this.replay.currentActivities;
		for (var B = 0; B < $.length; B++) {
			var A = $[B];
			if (A === _)
				return true
		}
		return false
	}
};
REPLAY_TOKEN_IMAGE = "user.png";
Replay = function($, A, _, B) {
	this.processDefinition = $;
	this.historyActivities = A;
	this.currentActivities = _;
	this.tokens = [];
	this.map = {};
	this.initialize();
	this.jobExecutor = new JobExecutor(this);
	this.parent = B
};
Replay.prototype = {
	initialize : function() {
		for (var A = 0; A < this.processDefinition.length; A++) {
			var _ = this.processDefinition[A];
			if (_.type === "start") {
				var $ = new Node(_, this);
				this.init = $;
				this.tokens.push(new Token($, this, this.parent));
				break
			}
		}
	},
	notify : function(_) {
		if (_ !== 0) {
			var A = Array.prototype.slice.call(this.tokens, 0);
			for (var B = 0; B < A.length; B++) {
				var $ = A[B];
				if ($.startMove(_) === true)
					this.jobExecutor.start()
			}
		}
	},
	prev : function() {
		this.notify(-1)
	},
	next : function() {
		this.notify(1)
	},
	replay : function() {
		this.destoryToken();
		this.tokens = [new Token(this.init, this, this.parent)];
		this.notify(this.processDefinition.length)
	},
	destoryToken : function() {
		this.jobExecutor.running = false;
		for (var _ = 0; _ < this.tokens.length; _++) {
			var $ = this.tokens[_];
			$.destroy()
		}
		delete this.tokens
	}
};
Token = function(_, $, A) {
	this.replay = $;
	this.src = _;
	this.status = "prepare";
	this.future = 0;
	this.forkIndex = 0;
	this.step = 10;
	this.parent = A
};
Token.prototype = {
	init : function() {
		this.x = this.src.x + this.src.w / 2 - 10;
		this.y = this.src.y + this.src.h / 2 - 10;
		if (this.status === "prepare") {
			this.status = "waiting";
			this.createImage()
		}
	},
	createImage : function() {
		var $ = document.createElement("img");
		this.parent.appendChild($);
		$.style.position = "absolute";
		$.src = REPLAY_TOKEN_IMAGE;
		$.style.left = this.x + "px";
		$.style.top = this.y + "px";
		this.dom = $
	},
	findNext : function() {
		return this.src.children
	},
	findPrev : function() {
		return this.src.parent
	},
	startMove : function(B) {
		if (B === 0)
			return false;
		if (this.status === "waiting" || this.status === "prepare") {
			var A = B > 0 ? this.findNext() : this.findPrev();
			if (A.length === 0) {
				this.future = 0;
				return false
			}
			for (var C = 0; C < A.length; C++) {
				var $ = A[C], _ = this;
				if (C !== 0) {
					_ = new Token(this.src, replay, this.parent);
					this.replay.tokens.push(_)
				}
				_.forkIndex = this.forkIndex + C;
				_.prepare($, B)
			}
			return true
		} else {
			this.future += B;
			return false
		}
	},
	prepare : function($, _) {
		this.init();
		this.dest = $;
		this.future = _;
		this.status = "running";
		this.step = 0;
		this.calculatePoints()
	},
	calculatePoints : function() {
		var H = this.src.x + this.src.w / 2 - 10, A = this.src.y + this.src.h
				/ 2 - 10, E = this.dest.x + this.dest.w / 2 - 10, B = this.dest.y
				+ this.dest.h / 2 - 10;
		this.points = [[H, A]];
		var D = this.findTransition();
		if (D.length == 0) {
			var $ = (E - H) / 10, _ = (B - A) / 10;
			for (var G = 0; G < 10; G++)
				this.points.push([H + $ * (G + 1), A + _ * (G + 1)])
		} else if (D.length == 1) {
			var F = D[0][0] - 10, C = D[0][1] - 10, $ = (F - H) / 5, _ = (C - A)
					/ 5;
			for (G = 0; G < 5; G++)
				this.points.push([H + $ * (G + 1), A + _ * (G + 1)]);
			$ = (E - F) / 5;
			_ = (B - C) / 5;
			for (G = 0; G < 5; G++)
				this.points.push([F + $ * (G + 1), C + _ * (G + 1)])
		}
	},
	findTransition : function() {
		var $ = null;
		if (this.future > 0)
			$ = this.findTransitionByParent();
		else if (this.future < 0)
			$ = this.findTransitionByChild();
		if (!$)
			$ = [];
		return $
	},
	findTransitionByParent : function() {
		for (var B = 0; B < this.dest.parent.length; B++) {
			var _ = this.dest.parent[B];
			if (this.src == _)
				for (var A = 0; A < _.activity.ts.length; A++) {
					var $ = _.activity.ts[A];
					if ($.to == this.dest.activity.name)
						return $.g
				}
		}
		return null
	},
	findTransitionByChild : function() {
		for (var C = 0; C < this.dest.children.length; C++) {
			var A = this.dest.children[C];
			if (this.src == A)
				for (var B = 0; B < this.dest.activity.ts.length; B++) {
					var _ = this.dest.activity.ts[B];
					if (_.to == A.activity.name) {
						if (!_.g)
							return null;
						var $ = [];
						for (C = _.g.length - 1; C >= 0; C--)
							$.push(_.g[C]);
						return $
					}
				}
		}
		return null
	},
	move : function() {
		this.step++;
		if (this.step > 10) {
			if (this.future !== 0)
				if (this.future > 0)
					this.future--;
				else
					this.future++;
			var $ = this.dest;
			if (this.forkIndex > 0)
				if ($.type == "fork" || $.type == "join") {
					this.destroy();
					return
				}
			this.src = $;
			this.init();
			this.status = "waiting";
			this.startMove(this.future)
		} else {
			this.dom.style.left = this.points[this.step][0] + "px";
			this.dom.style.top = this.points[this.step][1] + "px"
		}
	},
	destroy : function() {
		if (typeof this.dom !== "undefined") {
			document.body.removeChild(this.dom);
			delete this.dom
		}
		this.status = "removed"
	}
};
Raphael = (function() {
	var j = /[, ]+/, C0 = /^(circle|rect|path|ellipse|text|image)$/, O = "prototype", Q = "hasOwnProperty", T = document, U0 = window, n = {
		was : Object[O][Q].call(U0, "Raphael"),
		is : U0.Raphael
	}, k0 = function() {
		if (k0.is(arguments[0], "array")) {
			var _ = arguments[0], A = z[c](k0, _.splice(0, 3 + k0.is(_[0], g0))), B = A
					.set();
			for (var C = 0, D = _[k]; C < D; C++) {
				var $ = _[C] || {};
				C0.test($.type) && B[f](A[$.type]().attr($))
			}
			return B
		}
		return z[c](k0, arguments)
	}, X = function() {
	}, I0 = "appendChild", c = "apply", V = "concat", T0 = "", e0 = " ", E = "split", L = "click dblclick mousedown mousemove mouseout mouseover mouseup"[E](e0), a0 = "join", k = "length", A0 = String[O].toLowerCase, $1 = Math, q = $1.max, D0 = $1.min, g0 = "number", M0 = "toString", X0 = Object[O][M0], D = {}, H0 = $1.pow, f = "push", Z = /^(?=[\da-f]$)/, i = /^url\(['"]?([^\)]+?)['"]?\)$/i, B = /^\s*((#[a-f\d]{6})|(#[a-f\d]{3})|rgb\(\s*([\d\.]+\s*,\s*[\d\.]+\s*,\s*[\d\.]+)\s*\)|rgb\(\s*([\d\.]+%\s*,\s*[\d\.]+%\s*,\s*[\d\.]+%)\s*\)|hs[bl]\(\s*([\d\.]+\s*,\s*[\d\.]+\s*,\s*[\d\.]+)\s*\)|hs[bl]\(\s*([\d\.]+%\s*,\s*[\d\.]+%\s*,\s*[\d\.]+%)\s*\))\s*$/i, N = $1.round, $0 = "setAttribute", v0 = parseFloat, M = parseInt, J0 = String[O].toUpperCase, p = {
		blur : 0,
		"clip-rect" : "0 0 1e9 1e9",
		cursor : "default",
		cx : 0,
		cy : 0,
		fill : "#fff",
		"fill-opacity" : 1,
		font : "10px \"Arial\"",
		"font-family" : "\"Arial\"",
		"font-size" : "10",
		"font-style" : "normal",
		"font-weight" : 400,
		gradient : 0,
		height : 0,
		href : "http://raphaeljs.com/",
		opacity : 1,
		path : "M0,0",
		r : 0,
		rotation : 0,
		rx : 0,
		ry : 0,
		scale : "1 1",
		src : "",
		stroke : "#000",
		"stroke-dasharray" : "",
		"stroke-linecap" : "butt",
		"stroke-linejoin" : "butt",
		"stroke-miterlimit" : 0,
		"stroke-opacity" : 1,
		"stroke-width" : 1,
		target : "_blank",
		"text-anchor" : "middle",
		title : "Raphael",
		translation : "0 0",
		width : 0,
		x : 0,
		y : 0
	}, u0 = {
		along : "along",
		blur : g0,
		"clip-rect" : "csv",
		cx : g0,
		cy : g0,
		fill : "colour",
		"fill-opacity" : g0,
		"font-size" : g0,
		height : g0,
		opacity : g0,
		path : "path",
		r : g0,
		rotation : "csv",
		rx : g0,
		ry : g0,
		scale : "csv",
		stroke : "colour",
		"stroke-opacity" : g0,
		"stroke-width" : g0,
		translation : "csv",
		width : g0,
		x : g0,
		y : g0
	}, B0 = "replace";
	k0.version = "1.3.2";
	k0.type = (U0.SVGAngle
			|| T.implementation.hasFeature(
					"http://www.w3.org/TR/SVG11/feature#BasicStructure", "1.1")
			? "SVG"
			: "VML");
	if (k0.type == "VML") {
		var l0 = T.createElement("div");
		l0.innerHTML = "<!--[if vml]><br><br><![endif]-->";
		if (l0.childNodes[k] != 2)
			return k0.type = null;
		l0 = null
	}
	k0.svg = !(k0.vml = k0.type == "VML");
	X[O] = k0[O];
	k0._id = 0;
	k0._oid = 0;
	k0.fn = {};
	k0.is = function(_, $) {
		$ = A0.call($);
		return (($ == "object" || $ == "undefined") && typeof _ == $)
				|| (_ == null && $ == "null")
				|| A0.call(X0.call(_).slice(8, -1)) == $
	};
	k0.setWindow = function($) {
		U0 = $;
		T = U0.document
	};
	var P0 = function(A) {
		if (k0.vml) {
			var _ = /^\s+|\s+$/g;
			P0 = s0(function(A) {
						var $;
						A = (A + T0)[B0](_, T0);
						try {
							var B = new U0.ActiveXObject("htmlfile");
							B.write("<body>");
							B.close();
							$ = B.body
						} catch (D) {
							$ = U0.createPopup().document.body
						}
						var E = $.createTextRange();
						try {
							$.style.color = A;
							var C = E.queryCommandValue("ForeColor");
							C = ((C & 255) << 16) | (C & 65280)
									| ((C & 16711680) >>> 16);
							return "#" + ("000000" + C[M0](16)).slice(-6)
						} catch (D) {
							return "none"
						}
					})
		} else {
			var $ = T.createElement("i");
			$.title = "Rapha\xebl Colour Picker";
			$.style.display = "none";
			T.body[I0]($);
			P0 = s0(function(_) {
						$.style.color = _;
						return T.defaultView.getComputedStyle($, T0)
								.getPropertyValue("color")
					})
		}
		return P0(A)
	}, p0 = function() {
		return "hsb(" + [this.h, this.s, this.b] + ")"
	}, r = function() {
		return this.hex
	};
	k0.hsb2rgb = s0(function(G, L, B) {
				if (k0.is(G, "object") && "h" in G && "s" in G && "b" in G) {
					B = G.b;
					L = G.s;
					G = G.h
				}
				var F, D, C;
				if (B == 0)
					return {
						r : 0,
						g : 0,
						b : 0,
						hex : "#000"
					};
				if (G > 1 || L > 1 || B > 1) {
					G /= 255;
					L /= 255;
					B /= 255
				}
				var K = ~~(G * 6), H = (G * 6) - K, $ = B * (1 - L), A = B
						* (1 - (L * H)), E = B * (1 - (L * (1 - H)));
				F = [B, A, $, $, E, B, B][K];
				D = [E, B, B, A, $, $, E][K];
				C = [$, $, E, B, B, A, $][K];
				F *= 255;
				D *= 255;
				C *= 255;
				var I = {
					r : F,
					g : D,
					b : C,
					toString : r
				}, _ = (~~F)[M0](16), M = (~~D)[M0](16), J = (~~C)[M0](16);
				_ = _[B0](Z, "0");
				M = M[B0](Z, "0");
				J = J[B0](Z, "0");
				I.hex = "#" + _ + M + J;
				return I
			}, k0);
	k0.rgb2hsb = s0(function(_, A, F) {
				if (k0.is(_, "object") && "r" in _ && "g" in _ && "b" in _) {
					F = _.b;
					A = _.g;
					_ = _.r
				}
				if (k0.is(_, "string")) {
					var D = k0.getRGB(_);
					_ = D.r;
					A = D.g;
					F = D.b
				}
				if (_ > 1 || A > 1 || F > 1) {
					_ /= 255;
					A /= 255;
					F /= 255
				}
				var E = q(_, A, F), H = D0(_, A, F), C, $, B = E;
				if (H == E)
					return {
						h : 0,
						s : 0,
						b : E
					};
				else {
					var G = (E - H);
					$ = G / E;
					if (_ == E)
						C = (A - F) / G;
					else if (A == E)
						C = 2 + ((F - _) / G);
					else
						C = 4 + ((_ - A) / G);
					C /= 6;
					C < 0 && C++;
					C > 1 && C--
				}
				return {
					h : C,
					s : $,
					b : B,
					toString : p0
				}
			}, k0);
	var Q0 = /,?([achlmqrstvxz]),?/gi;
	k0._path2string = function() {
		return this.join(",")[B0](Q0, "$1")
	};
	function s0($, A, _) {
		function B() {
			var D = Array[O].slice.call(arguments, 0), E = D[a0]("\u25ba"), C = B.cache = B.cache
					|| {}, F = B.count = B.count || [];
			if (C[Q](E))
				return _ ? _(C[E]) : C[E];
			F[k] >= 1000 && delete C[F.shift()];
			F[f](E);
			C[E] = $[c](A, D);
			return _ ? _(C[E]) : C[E]
		}
		return B
	}
	k0.getRGB = s0(function(_) {
				if (!_ || !!((_ = _ + T0).indexOf("-") + 1))
					return {
						r : -1,
						g : -1,
						b : -1,
						hex : "none",
						error : 1
					};
				if (_ == "none")
					return {
						r : -1,
						g : -1,
						b : -1,
						hex : "none"
					};
				!(({
					hs : 1,
					rg : 1
				})[Q](_.substring(0, 2)) || _.charAt() == "#") && (_ = P0(_));
				var C, J, $, I, F, G = _.match(B);
				if (G) {
					if (G[2]) {
						I = M(G[2].substring(5), 16);
						$ = M(G[2].substring(3, 5), 16);
						J = M(G[2].substring(1, 3), 16)
					}
					if (G[3]) {
						I = M((F = G[3].charAt(3)) + F, 16);
						$ = M((F = G[3].charAt(2)) + F, 16);
						J = M((F = G[3].charAt(1)) + F, 16)
					}
					if (G[4]) {
						G = G[4][E](/\s*,\s*/);
						J = v0(G[0]);
						$ = v0(G[1]);
						I = v0(G[2])
					}
					if (G[5]) {
						G = G[5][E](/\s*,\s*/);
						J = v0(G[0]) * 2.55;
						$ = v0(G[1]) * 2.55;
						I = v0(G[2]) * 2.55
					}
					if (G[6]) {
						G = G[6][E](/\s*,\s*/);
						J = v0(G[0]);
						$ = v0(G[1]);
						I = v0(G[2]);
						return k0.hsb2rgb(J, $, I)
					}
					if (G[7]) {
						G = G[7][E](/\s*,\s*/);
						J = v0(G[0]) * 2.55;
						$ = v0(G[1]) * 2.55;
						I = v0(G[2]) * 2.55;
						return k0.hsb2rgb(J, $, I)
					}
					G = {
						r : J,
						g : $,
						b : I
					};
					var A = (~~J)[M0](16), D = (~~$)[M0](16), H = (~~I)[M0](16);
					A = A[B0](Z, "0");
					D = D[B0](Z, "0");
					H = H[B0](Z, "0");
					G.hex = "#" + A + D + H;
					return G
				}
				return {
					r : -1,
					g : -1,
					b : -1,
					hex : "none",
					error : 1
				}
			}, k0);
	k0.getColor = function(_) {
		var A = this.getColor.start = this.getColor.start || {
			h : 0,
			s : 1,
			b : _ || 0.75
		}, $ = this.hsb2rgb(A.h, A.s, A.b);
		A.h += 0.075;
		if (A.h > 1) {
			A.h = 0;
			A.s -= 0.2;
			A.s <= 0 && (this.getColor.start = {
				h : 0,
				s : 1,
				b : A.b
			})
		}
		return $.hex
	};
	k0.getColor.reset = function() {
		delete this.start
	};
	var V0 = /([achlmqstvz])[\s,]*((-?\d*\.?\d*(?:e[-+]?\d+)?\s*,?\s*)+)/ig, f0 = /(-?\d*\.?\d*(?:e[-+]?\d+)?)\s*,?\s*/ig;
	k0.parsePathString = s0(function($) {
				if (!$)
					return null;
				var A = {
					a : 7,
					c : 6,
					h : 1,
					l : 2,
					m : 2,
					q : 4,
					s : 4,
					t : 2,
					v : 1,
					z : 0
				}, _ = [];
				if (k0.is($, "array") && k0.is($[0], "array"))
					_ = W0($);
				if (!_[k])
					($ + T0)[B0](V0, function(C, $, E) {
								var D = [], B = A0.call($);
								E[B0](f0, function($, _) {
											_ && D[f](+_)
										});
								if (B == "m" && D[k] > 2) {
									_[f]([$][V](D.splice(0, 2)));
									B = "l";
									$ = $ == "m" ? "l" : "L"
								}
								while (D[k] >= A[B]) {
									_[f]([$][V](D.splice(0, A[B])));
									if (!A[B])
										break
								}
							});
				_[M0] = k0._path2string;
				return _
			});
	k0.findDotsAtSegment = function(A, _, F, D, Q, L, S, R, H) {
		var P = 1 - H, O = H0(P, 3) * A + H0(P, 2) * 3 * H * F + P * 3 * H * H
				* Q + H0(H, 3) * S, M = H0(P, 3) * _ + H0(P, 2) * 3 * H * D + P
				* 3 * H * H * L + H0(H, 3) * R, K = A + 2 * H * (F - A) + H * H
				* (Q - 2 * F + A), J = _ + 2 * H * (D - _) + H * H
				* (L - 2 * D + _), E = F + 2 * H * (Q - F) + H * H
				* (S - 2 * Q + F), C = D + 2 * H * (L - D) + H * H
				* (R - 2 * L + D), G = (1 - H) * A + H * F, N = (1 - H) * _ + H
				* D, $ = (1 - H) * Q + H * S, B = (1 - H) * L + H * R, I = (90 - $1
				.atan((K - E) / (J - C))
				* 180 / $1.PI);
		(K > E || J < C) && (I += 180);
		return {
			x : O,
			y : M,
			m : {
				x : K,
				y : J
			},
			n : {
				x : E,
				y : C
			},
			start : {
				x : G,
				y : N
			},
			end : {
				x : $,
				y : B
			},
			alpha : I
		}
	};
	var Y = s0(function(G) {
				if (!G)
					return {
						x : 0,
						y : 0,
						width : 0,
						height : 0
					};
				G = F(G);
				var J = 0, I = 0, C = [], A = [], $;
				for (var B = 0, E = G[k]; B < E; B++) {
					$ = G[B];
					if ($[0] == "M") {
						J = $[1];
						I = $[2];
						C[f](J);
						A[f](I)
					} else {
						var H = O0(J, I, $[1], $[2], $[3], $[4], $[5], $[6]);
						C = C[V](H.min.x, H.max.x);
						A = A[V](H.min.y, H.max.y);
						J = $[5];
						I = $[6]
					}
				}
				var _ = D0[c](0, C), D = D0[c](0, A);
				return {
					x : _,
					y : D,
					width : q[c](0, C) - _,
					height : q[c](0, A) - D
				}
			}), W0 = function(D) {
		var $ = [];
		if (!k0.is(D, "array") || !k0.is(D && D[0], "array"))
			D = k0.parsePathString(D);
		for (var A = 0, C = D[k]; A < C; A++) {
			$[A] = [];
			for (var _ = 0, B = D[A][k]; _ < B; _++)
				$[A][_] = D[A][_]
		}
		$[M0] = k0._path2string;
		return $
	}, y0 = s0(function(F) {
				if (!k0.is(F, "array") || !k0.is(F && F[0], "array"))
					F = k0.parsePathString(F);
				var I = [], K = 0, J = 0, E = 0, C = 0, $ = 0;
				if (F[0][0] == "M") {
					K = F[0][1];
					J = F[0][2];
					E = K;
					C = J;
					$++;
					I[f](["M", K, J])
				}
				for (var M = $, G = F[k]; M < G; M++) {
					var _ = I[M] = [], B = F[M];
					if (B[0] != A0.call(B[0])) {
						_[0] = A0.call(B[0]);
						switch (_[0]) {
							case "a" :
								_[1] = B[1];
								_[2] = B[2];
								_[3] = B[3];
								_[4] = B[4];
								_[5] = B[5];
								_[6] = +(B[6] - K).toFixed(3);
								_[7] = +(B[7] - J).toFixed(3);
								break;
							case "v" :
								_[1] = +(B[1] - J).toFixed(3);
								break;
							case "m" :
								E = B[1];
								C = B[2];
							default :
								for (var L = 1, N = B[k]; L < N; L++)
									_[L] = +(B[L] - ((L % 2) ? K : J))
											.toFixed(3)
						}
					} else {
						_ = I[M] = [];
						if (B[0] == "m") {
							E = B[1] + K;
							C = B[2] + J
						}
						for (var D = 0, A = B[k]; D < A; D++)
							I[M][D] = B[D]
					}
					var H = I[M][k];
					switch (I[M][0]) {
						case "z" :
							K = E;
							J = C;
							break;
						case "h" :
							K += +I[M][H - 1];
							break;
						case "v" :
							J += +I[M][H - 1];
							break;
						default :
							K += +I[M][H - 2];
							J += +I[M][H - 1]
					}
				}
				I[M0] = k0._path2string;
				return I
			}, 0, W0), u = s0(function(F) {
				if (!k0.is(F, "array") || !k0.is(F && F[0], "array"))
					F = k0.parsePathString(F);
				var G = [], I = 0, H = 0, C = 0, B = 0, $ = 0;
				if (F[0][0] == "M") {
					I = +F[0][1];
					H = +F[0][2];
					C = I;
					B = H;
					$++;
					G[0] = ["M", I, H]
				}
				for (var L = $, E = F[k]; L < E; L++) {
					var _ = G[L] = [], J = F[L];
					if (J[0] != J0.call(J[0])) {
						_[0] = J0.call(J[0]);
						switch (_[0]) {
							case "A" :
								_[1] = J[1];
								_[2] = J[2];
								_[3] = J[3];
								_[4] = J[4];
								_[5] = J[5];
								_[6] = +(J[6] + I);
								_[7] = +(J[7] + H);
								break;
							case "V" :
								_[1] = +J[1] + H;
								break;
							case "H" :
								_[1] = +J[1] + I;
								break;
							case "M" :
								C = +J[1] + I;
								B = +J[2] + H;
							default :
								for (var K = 1, M = J[k]; K < M; K++)
									_[K] = +J[K] + ((K % 2) ? I : H)
						}
					} else
						for (var D = 0, A = J[k]; D < A; D++)
							G[L][D] = J[D];
					switch (_[0]) {
						case "Z" :
							I = C;
							H = B;
							break;
						case "H" :
							I = _[1];
							break;
						case "V" :
							H = _[1];
							break;
						default :
							I = G[L][G[L][k] - 2];
							H = G[L][G[L][k] - 1]
					}
				}
				G[M0] = k0._path2string;
				return G
			}, null, W0), d = function(A, $, _, B) {
		return [A, $, _, B, _, B]
	}, F0 = function(A, $, D, C, _, F) {
		var B = 1 / 3, E = 2 / 3;
		return [B * A + E * D, B * $ + E * C, B * _ + E * D, B * F + E * C, _,
				F]
	}, I = function(S, l, J, G, U, X, R, Q, j, L) {
		var T = $1.PI, H = T * 120 / 180, d = T / 180 * (+U || 0), D = [], C, g = s0(
				function(B, $, C) {
					var _ = B * $1.cos(C) - $ * $1.sin(C), A = B * $1.sin(C)
							+ $ * $1.cos(C);
					return {
						x : _,
						y : A
					}
				});
		if (!L) {
			C = g(S, l, -d);
			S = C.x;
			l = C.y;
			C = g(Q, j, -d);
			Q = C.x;
			j = C.y;
			var e = $1.cos(T / 180 * U), Z = $1.sin(T / 180 * U), A = (S - Q)
					/ 2, _ = (l - j) / 2, i = (A * A) / (J * J) + (_ * _)
					/ (G * G);
			if (i > 1) {
				i = $1.sqrt(i);
				J = i * J;
				G = i * G
			}
			var $ = J * J, o = G * G, u = (X == R ? -1 : 1)
					* $1.sqrt($1.abs(($ * o - $ * _ * _ - o * A * A)
							/ ($ * _ * _ + o * A * A))), N = u * J * _ / G
					+ (S + Q) / 2, M = u * -G * A / J + (l + j) / 2, W = $1
					.asin(((l - M) / G).toFixed(7)), c = $1.asin(((j - M) / G)
					.toFixed(7));
			W = S < N ? T - W : W;
			c = Q < N ? T - c : c;
			W < 0 && (W = T * 2 + W);
			c < 0 && (c = T * 2 + c);
			if (R && W > c)
				W = W - T * 2;
			if (!R && c > W)
				c = c - T * 2
		} else {
			W = L[0];
			c = L[1];
			N = L[2];
			M = L[3]
		}
		var P = c - W;
		if ($1.abs(P) > H) {
			var O = c, K = Q, Y = j;
			c = W + H * (R && c > W ? 1 : -1);
			Q = N + J * $1.cos(c);
			j = M + G * $1.sin(c);
			D = I(Q, j, J, G, U, 0, R, K, Y, [c, O, N, M])
		}
		P = c - W;
		var b = $1.cos(W), n = $1.sin(W), a = $1.cos(c), m = $1.sin(c), q = $1
				.tan(P / 4), p = 4 / 3 * J * q, r = 4 / 3 * G * q, f = [S, l], h = [
				S + p * n, l - r * b], t = [Q + p * m, j - r * a], v = [Q, j];
		h[0] = 2 * f[0] - h[0];
		h[1] = 2 * f[1] - h[1];
		if (L)
			return [h, t, v][V](D);
		else {
			D = [h, t, v][V](D)[a0]()[E](",");
			var B = [];
			for (var s = 0, F = D[k]; s < F; s++)
				B[s] = s % 2 ? g(D[s - 1], D[s], d).y : g(D[s], D[s + 1], d).x;
			return B
		}
	}, P = function(A, _, $, H, G, F, E, B, D) {
		var C = 1 - D;
		return {
			x : H0(C, 3) * A + H0(C, 2) * 3 * D * $ + C * 3 * D * D * G
					+ H0(D, 3) * E,
			y : H0(C, 3) * _ + H0(C, 2) * 3 * D * H + C * 3 * D * D * F
					+ H0(D, 3) * B
		}
	}, O0 = s0(function(B, _, G, $, F, D, J, N) {
		var C = (F - 2 * G + B) - (J - 2 * F + G), I = 2 * (G - B) - 2
				* (F - G), M = B - G, L = (-I + $1.sqrt(I * I - 4 * C * M)) / 2
				/ C, E = (-I - $1.sqrt(I * I - 4 * C * M)) / 2 / C, H = [_, N], K = [
				B, J], A;
		$1.abs(L) > 1000000000000 && (L = 0.5);
		$1.abs(E) > 1000000000000 && (E = 0.5);
		if (L > 0 && L < 1) {
			A = P(B, _, G, $, F, D, J, N, L);
			K[f](A.x);
			H[f](A.y)
		}
		if (E > 0 && E < 1) {
			A = P(B, _, G, $, F, D, J, N, E);
			K[f](A.x);
			H[f](A.y)
		}
		C = (D - 2 * $ + _) - (N - 2 * D + $);
		I = 2 * ($ - _) - 2 * (D - $);
		M = _ - $;
		L = (-I + $1.sqrt(I * I - 4 * C * M)) / 2 / C;
		E = (-I - $1.sqrt(I * I - 4 * C * M)) / 2 / C;
		$1.abs(L) > 1000000000000 && (L = 0.5);
		$1.abs(E) > 1000000000000 && (E = 0.5);
		if (L > 0 && L < 1) {
			A = P(B, _, G, $, F, D, J, N, L);
			K[f](A.x);
			H[f](A.y)
		}
		if (E > 0 && E < 1) {
			A = P(B, _, G, $, F, D, J, N, E);
			K[f](A.x);
			H[f](A.y)
		}
		return {
			min : {
				x : D0[c](0, K),
				y : D0[c](0, H)
			},
			max : {
				x : q[c](0, K),
				y : q[c](0, H)
			}
		}
	}), F = s0(function(E, H) {
				var F = u(E), J = H && u(H), K = {
					x : 0,
					y : 0,
					bx : 0,
					by : 0,
					X : 0,
					Y : 0,
					qx : null,
					qy : null
				}, _ = {
					x : 0,
					y : 0,
					bx : 0,
					by : 0,
					X : 0,
					Y : 0,
					qx : null,
					qy : null
				}, L = function(A, $) {
					var B, _;
					if (!A)
						return ["C", $.x, $.y, $.x, $.y, $.x, $.y];
					!(A[0] in {
						T : 1,
						Q : 1
					}) && ($.qx = $.qy = null);
					switch (A[0]) {
						case "M" :
							$.X = A[1];
							$.Y = A[2];
							break;
						case "A" :
							A = ["C"][V](I[c](0, [$.x, $.y][V](A.slice(1))));
							break;
						case "S" :
							B = $.x + ($.x - ($.bx || $.x));
							_ = $.y + ($.y - ($.by || $.y));
							A = ["C", B, _][V](A.slice(1));
							break;
						case "T" :
							$.qx = $.x + ($.x - ($.qx || $.x));
							$.qy = $.y + ($.y - ($.qy || $.y));
							A = ["C"][V](F0($.x, $.y, $.qx, $.qy, A[1], A[2]));
							break;
						case "Q" :
							$.qx = A[1];
							$.qy = A[2];
							A = ["C"][V](F0($.x, $.y, A[1], A[2], A[3], A[4]));
							break;
						case "L" :
							A = ["C"][V](d($.x, $.y, A[1], A[2]));
							break;
						case "H" :
							A = ["C"][V](d($.x, $.y, A[1], $.y));
							break;
						case "V" :
							A = ["C"][V](d($.x, $.y, $.x, A[1]));
							break;
						case "Z" :
							A = ["C"][V](d($.x, $.y, $.X, $.Y));
							break
					}
					return A
				}, A = function(A, $) {
					if (A[$][k] > 7) {
						A[$].shift();
						var _ = A[$];
						while (_[k])
							A.splice($++, 0, ["C"][V](_.splice(0, 6)));
						A.splice($, 1);
						B = q(F[k], J && J[k] || 0)
					}
				}, $ = function(C, A, $, D, _) {
					if (C && A && C[_][0] == "M" && A[_][0] != "M") {
						A.splice(_, 0, ["M", D.x, D.y]);
						$.bx = 0;
						$.by = 0;
						$.x = C[_][1];
						$.y = C[_][2];
						B = q(F[k], J && J[k] || 0)
					}
				};
				for (var N = 0, B = q(F[k], J && J[k] || 0); N < B; N++) {
					F[N] = L(F[N], K);
					A(F, N);
					J && (J[N] = L(J[N], _));
					J && A(J, N);
					$(F, J, K, _, N);
					$(J, F, _, K, N);
					var M = F[N], C = J && J[N], D = M[k], G = J && C[k];
					K.x = M[D - 2];
					K.y = M[D - 1];
					K.bx = v0(M[D - 4]) || K.x;
					K.by = v0(M[D - 3]) || K.y;
					_.bx = J && (v0(C[G - 4]) || _.x);
					_.by = J && (v0(C[G - 3]) || _.y);
					_.x = J && C[G - 2];
					_.y = J && C[G - 1]
				}
				return J ? [F, J] : F
			}, null, W0), w = s0(function(D) {
				var C = [];
				for (var F = 0, E = D[k]; F < E; F++) {
					var _ = {}, H = D[F].match(/^([^:]*):?([\d\.]*)/);
					_.color = k0.getRGB(H[1]);
					if (_.color.error)
						return null;
					_.color = _.color.hex;
					H[2] && (_.offset = H[2] + "%");
					C[f](_)
				}
				for (F = 1, E = C[k] - 1; F < E; F++)
					if (!C[F].offset) {
						var $ = v0(C[F - 1].offset || 0), B = 0;
						for (var A = F + 1; A < E; A++)
							if (C[A].offset) {
								B = C[A].offset;
								break
							}
						if (!B) {
							B = 100;
							A = E
						}
						B = v0(B);
						var G = (B - $) / (A - F + 1);
						for (; F < A; F++) {
							$ += G;
							C[F].offset = $ + "%"
						}
					}
				return C
			}), j0 = function(_, B, C, $) {
		var A;
		if (k0.is(_, "string") || k0.is(_, "object")) {
			A = k0.is(_, "string") ? T.getElementById(_) : _;
			if (A.tagName)
				if (B == null)
					return {
						container : A,
						width : A.style.pixelWidth || A.offsetWidth,
						height : A.style.pixelHeight || A.offsetHeight
					};
				else
					return {
						container : A,
						width : B,
						height : C
					}
		} else if (k0.is(_, g0) && $ != null)
			return {
				container : 1,
				x : _,
				y : B,
				width : C,
				height : $
			}
	}, S0 = function(_, B) {
		var A = this;
		for (var $ in B)
			if (B[Q]($) && !($ in _))
				switch (typeof B[$]) {
					case "function" :
						(function(B) {
							_[$] = _ === A ? B : function() {
								return B[c](A, arguments)
							}
						})(B[$]);
						break;
					case "object" :
						_[$] = _[$] || {};
						S0.call(this, _[$], B[$]);
						break;
					default :
						_[$] = B[$];
						break
				}
	}, q0 = function($, _) {
		$ == _.top && (_.top = $.prev);
		$ == _.bottom && (_.bottom = $.next);
		$.next && ($.next.prev = $.prev);
		$.prev && ($.prev.next = $.next)
	}, t0 = function($, _) {
		if (_.top === $)
			return;
		q0($, _);
		$.next = null;
		$.prev = _.top;
		_.top.next = $;
		_.top = $
	}, m = function($, _) {
		if (_.bottom === $)
			return;
		q0($, _);
		$.next = _.bottom;
		$.prev = null;
		_.bottom.prev = $;
		_.bottom = $
	}, $ = function(_, $, A) {
		q0(_, A);
		$ == A.top && (A.top = _);
		$.next && ($.next.prev = _);
		_.next = $.next;
		_.prev = $;
		$.next = _
	}, h0 = function(_, $, A) {
		q0(_, A);
		$ == A.bottom && (A.bottom = _);
		$.prev && ($.prev.next = _);
		_.prev = $.prev;
		$.prev = _;
		_.next = $
	}, t = function($) {
		return function() {
			throw new Error("Rapha\xebl: you are calling to method \u201c" + $
					+ "\u201d of removed object")
		}
	}, b0 = /^r(?:\(([^,]+?)\s*,\s*([^\)]+?)\))?/;
	if (k0.svg) {
		X[O].svgns = "http://www.w3.org/2000/svg";
		X[O].xlink = "http://www.w3.org/1999/xlink";
		N = function($) {
			return +$ + (~~$ === $) * 0.5
		};
		var e = function(B) {
			for (var A = 0, $ = B[k]; A < $; A++)
				if (A0.call(B[A][0]) != "a") {
					for (var _ = 1, C = B[A][k]; _ < C; _++)
						B[A][_] = N(B[A][_])
				} else {
					B[A][6] = N(B[A][6]);
					B[A][7] = N(B[A][7])
				}
			return B
		}, G0 = function(A, $) {
			if ($) {
				for (var _ in $)
					if ($[Q](_))
						A[$0](_, $[_] + T0)
			} else
				return T.createElementNS(X[O].svgns, A)
		};
		k0[M0] = function() {
			return "Your browser supports SVG.\nYou are running Rapha\xebl "
					+ this.version
		};
		var v = function(_, $) {
			var A = G0("path");
			$.canvas && $.canvas[I0](A);
			var B = new Y0(A, $);
			B.type = "path";
			z0(B, {
						fill : "none",
						stroke : "#000",
						path : _
					});
			return B
		}, h = function(G, C, _) {
			var K = "linear", O = 0.5, M = 0.5, H = G.style;
			C = (C + T0)[B0](b0, function(_, B, A) {
						K = "radial";
						if (B && A) {
							O = v0(B);
							M = v0(A);
							var $ = ((M > 0.5) * 2 - 1);
							H0(O - 0.5, 2) + H0(M - 0.5, 2) > 0.25
									&& (M = $1.sqrt(0.25 - H0(O - 0.5, 2)) * $
											+ 0.5) && M != 0.5
									&& (M = M.toFixed(5) - 0.00001 * $)
						}
						return T0
					});
			C = C[E](/\s*\-\s*/);
			if (K == "linear") {
				var N = C.shift();
				N = -v0(N);
				if (isNaN(N))
					return null;
				var F = [0, 0, $1.cos(N * $1.PI / 180), $1.sin(N * $1.PI / 180)], B = 1
						/ (q($1.abs(F[2]), $1.abs(F[3])) || 1);
				F[2] *= B;
				F[3] *= B;
				if (F[2] < 0) {
					F[0] = -F[2];
					F[2] = 0
				}
				if (F[3] < 0) {
					F[1] = -F[3];
					F[3] = 0
				}
			}
			var J = w(C);
			if (!J)
				return null;
			var A = G.getAttribute("fill");
			A = A.match(/^url\(#(.*)\)$/);
			A && _.defs.removeChild(T.getElementById(A[1]));
			var $ = G0(K + "Gradient");
			$.id = "r" + (k0._id++)[M0](36);
			G0($,	K == "radial" ? {
						fx : O,
						fy : M
					} : {
						x1 : F[0],
						y1 : F[1],
						x2 : F[2],
						y2 : F[3]
					});
			_.defs[I0]($);
			for (var I = 0, D = J[k]; I < D; I++) {
				var L = G0("stop");
				G0(L, {
							offset : J[I].offset ? J[I].offset : !I
									? "0%"
									: "100%",
							"stop-color" : J[I].color || "#fff"
						});
				$[I0](L)
			}
			G0(G, {
						fill : "url(#" + $.id + ")",
						opacity : 1,
						"fill-opacity" : 1
					});
			H.fill = T0;
			H.opacity = 1;
			H.fillOpacity = 1;
			return 1
		}, S = function(_) {
			var $ = _.getBBox();
			G0(_.pattern, {
						patternTransform : k0.format("translate({0},{1})", $.x,
								$.y)
					})
		}, z0 = function(Y, C) {
			var O = {
				"" : [0],
				none : [0],
				"-" : [3, 1],
				"." : [1, 1],
				"-." : [3, 1, 1, 1],
				"-.." : [3, 1, 1, 1, 1, 1],
				". " : [1, 3],
				"- " : [4, 3],
				"--" : [8, 3],
				"- ." : [4, 3, 1, 3],
				"--." : [8, 3, 1, 3],
				"--.." : [8, 3, 1, 3, 1, 3]
			}, F = Y.node, J = Y.attrs, V = Y.rotate(), P = function(A, B) {
				B = O[A0.call(B)];
				if (B) {
					var _ = A.attrs["stroke-width"] || "1", E = {
						round : _,
						square : _,
						butt : 0
					}[A.attrs["stroke-linecap"] || C["stroke-linecap"]] || 0, $ = [], D = B[k];
					while (D--)
						$[D] = B[D] * _ + ((D % 2) ? 1 : -1) * E;
					G0(F, {
								"stroke-dasharray" : $[a0](",")
							})
				}
			};
			C[Q]("rotation") && (V = C.rotation);
			var b = (V + T0)[E](j);
			if (!(b.length - 1))
				b = null;
			else {
				b[1] = +b[1];
				b[2] = +b[2]
			}
			v0(V) && Y.rotate(0, true);
			for (var U in C)
				if (C[Q](U)) {
					if (!p[Q](U))
						continue;
					var L = C[U];
					J[U] = L;
					switch (U) {
						case "blur" :
							Y.blur(L);
							break;
						case "rotation" :
							Y.rotate(L, true);
							break;
						case "href" :
						case "title" :
						case "target" :
							var H = F.parentNode;
							if (A0.call(H.tagName) != "a") {
								var $ = G0("a");
								H.insertBefore($, F);
								$[I0](F);
								H = $
							}
							H.setAttributeNS(Y.paper.xlink, U, L);
							break;
						case "cursor" :
							F.style.cursor = L;
							break;
						case "clip-rect" :
							var A = (L + T0)[E](j);
							if (A[k] == 4) {
								Y.clip
										&& Y.clip.parentNode.parentNode
												.removeChild(Y.clip.parentNode);
								var D = G0("clipPath"), G = G0("rect");
								D.id = "r" + (k0._id++)[M0](36);
								G0(G, {
											x : A[0],
											y : A[1],
											width : A[2],
											height : A[3]
										});
								D[I0](G);
								Y.paper.defs[I0](D);
								G0(F, {
											"clip-path" : "url(#" + D.id + ")"
										});
								Y.clip = G
							}
							if (!L) {
								var I = T.getElementById(F
										.getAttribute("clip-path")[B0](
										/(^url\(#|\)$)/g, T0));
								I && I.parentNode.removeChild(I);
								G0(F, {
											"clip-path" : T0
										});
								delete Y.clip
							}
							break;
						case "path" :
							if (Y.type == "path")
								G0(F, {
											d : L ? J.path = e(u(L)) : "M0,0"
										});
							break;
						case "width" :
							F[$0](U, L);
							if (J.fx) {
								U = "x";
								L = J.x
							} else
								break;
						case "x" :
							if (J.fx)
								L = -J.x - (J.width || 0);
						case "rx" :
							if (U == "rx" && Y.type == "rect")
								break;
						case "cx" :
							b && (U == "x" || U == "cx") && (b[1] += L - J[U]);
							F[$0](U, N(L));
							Y.pattern && S(Y);
							break;
						case "height" :
							F[$0](U, L);
							if (J.fy) {
								U = "y";
								L = J.y
							} else
								break;
						case "y" :
							if (J.fy)
								L = -J.y - (J.height || 0);
						case "ry" :
							if (U == "ry" && Y.type == "rect")
								break;
						case "cy" :
							b && (U == "y" || U == "cy") && (b[2] += L - J[U]);
							F[$0](U, N(L));
							Y.pattern && S(Y);
							break;
						case "r" :
							if (Y.type == "rect")
								G0(F, {
											rx : L,
											ry : L
										});
							else
								F[$0](U, L);
							break;
						case "src" :
							if (Y.type == "image")
								F.setAttributeNS(Y.paper.xlink, "href", L);
							break;
						case "stroke-width" :
							F.style.strokeWidth = L;
							F[$0](U, L);
							if (J["stroke-dasharray"])
								P(Y, J["stroke-dasharray"]);
							break;
						case "stroke-dasharray" :
							P(Y, L);
							break;
						case "translation" :
							var Z = (L + T0)[E](j);
							Z[0] = +Z[0] || 0;
							Z[1] = +Z[1] || 0;
							if (b) {
								b[1] += Z[0];
								b[2] += Z[1]
							}
							s.call(Y, Z[0], Z[1]);
							break;
						case "scale" :
							Z = (L + T0)[E](j);
							Y.scale(+Z[0] || 1, +Z[1] || +Z[0] || 1,
									isNaN(v0(Z[2])) ? null : +Z[2],
									isNaN(v0(Z[3])) ? null : +Z[3]);
							break;
						case "fill" :
							var R = (L + T0).match(i);
							if (R) {
								D = G0("pattern");
								var X = G0("image");
								D.id = "r" + (k0._id++)[M0](36);
								G0(D, {
											x : 0,
											y : 0,
											patternUnits : "userSpaceOnUse",
											height : 1,
											width : 1
										});
								G0(X, {
											x : 0,
											y : 0
										});
								X.setAttributeNS(Y.paper.xlink, "href", R[1]);
								D[I0](X);
								var B = T.createElement("img");
								B.style.cssText = "position:absolute;left:-9999em;top-9999em";
								B.onload = function() {
									G0(D, {
												width : this.offsetWidth,
												height : this.offsetHeight
											});
									G0(X, {
												width : this.offsetWidth,
												height : this.offsetHeight
											});
									T.body.removeChild(this);
									Y.paper.safari()
								};
								T.body[I0](B);
								B.src = R[1];
								Y.paper.defs[I0](D);
								F.style.fill = "url(#" + D.id + ")";
								G0(F, {
											fill : "url(#" + D.id + ")"
										});
								Y.pattern = D;
								Y.pattern && S(Y);
								break
							}
							if (!k0.getRGB(L).error) {
								delete C.gradient;
								delete J.gradient;
								!k0.is(J.opacity, "undefined")
										&& k0.is(C.opacity, "undefined")
										&& G0(F, {
													opacity : J.opacity
												});
								!k0.is(J["fill-opacity"], "undefined")
										&& k0
												.is(C["fill-opacity"],
														"undefined") && G0(F, {
													"fill-opacity" : J["fill-opacity"]
												})
							} else if ((({
								circle : 1,
								ellipse : 1
							})[Q](Y.type) || (L + T0).charAt() != "r")
									&& h(F, L, Y.paper)) {
								J.gradient = L;
								J.fill = "none";
								break
							}
						case "stroke" :
							F[$0](U, k0.getRGB(L).hex);
							break;
						case "gradient" :
							(({
								circle : 1,
								ellipse : 1
							})[Q](Y.type) || (L + T0).charAt() != "r")
									&& h(F, L, Y.paper);
							break;
						case "opacity" :
						case "fill-opacity" :
							if (J.gradient) {
								var _ = T
										.getElementById(F.getAttribute("fill")[B0](
												/^url\(#|\)$/g, T0));
								if (_) {
									var a = _.getElementsByTagName("stop");
									a[a[k] - 1][$0]("stop-opacity", L)
								}
								break
							}
						default :
							U == "font-size" && (L = M(L, 10) + "px");
							var W = U[B0](/(\-.)/g, function($) {
										return J0.call($.substring(1))
									});
							F.style[W] = L;
							F[$0](U, L);
							break
					}
				}
			K(Y, C);
			if (b)
				Y.rotate(b.join(e0));
			else
				v0(V) && Y.rotate(V, true)
		}, o = 1.2, K = function(_, C) {
			if (_.type != "text"
					|| !(C[Q]("text") || C[Q]("font") || C[Q]("font-size")
							|| C[Q]("x") || C[Q]("y")))
				return;
			var D = _.attrs, A = _.node, G = A.firstChild ? M(T.defaultView
							.getComputedStyle(A.firstChild, T0)
							.getPropertyValue("font-size"), 10) : 10;
			if (C[Q]("text")) {
				D.text = C.text;
				while (A.firstChild)
					A.removeChild(A.firstChild);
				var $ = (C.text + T0)[E]("\n");
				for (var B = 0, F = $[k]; B < F; B++)
					if ($[B]) {
						var I = G0("tspan");
						B && G0(I, {
									dy : G * o,
									x : D.x
								});
						I[I0](T.createTextNode($[B]));
						A[I0](I)
					}
			} else {
				$ = A.getElementsByTagName("tspan");
				for (B = 0, F = $[k]; B < F; B++)
					B && G0($[B], {
								dy : G * o,
								x : D.x
							})
			}
			G0(A, {
						y : D.y
					});
			var H = _.getBBox(), J = D.y - (H.y + H.height / 2);
			J && isFinite(J) && G0(A, {
						y : D.y + J
					})
		}, Y0 = function(A, _) {
			var $ = 0, B = 0;
			this[0] = A;
			this.id = k0._oid++;
			this.node = A;
			A.raphael = this;
			this.paper = _;
			this.attrs = this.attrs || {};
			this.transformations = [];
			this._ = {
				tx : 0,
				ty : 0,
				rt : {
					deg : 0,
					cx : 0,
					cy : 0
				},
				sx : 1,
				sy : 1
			};
			!_.bottom && (_.bottom = this);
			this.prev = _.top;
			_.top && (_.top.next = this);
			_.top = this;
			this.next = null
		};
		Y0[O].rotate = function(A, _, $) {
			if (this.removed)
				return this;
			if (A == null) {
				if (this._.rt.cx)
					return [this._.rt.deg, this._.rt.cx, this._.rt.cy][a0](e0);
				return this._.rt.deg
			}
			var B = this.getBBox();
			A = (A + T0)[E](j);
			if (A[k] - 1) {
				_ = v0(A[1]);
				$ = v0(A[2])
			}
			A = v0(A[0]);
			if (_ != null)
				this._.rt.deg = A;
			else
				this._.rt.deg += A;
			($ == null) && (_ = null);
			this._.rt.cx = _;
			this._.rt.cy = $;
			_ = _ == null ? B.x + B.width / 2 : _;
			$ = $ == null ? B.y + B.height / 2 : $;
			if (this._.rt.deg) {
				this.transformations[0] = k0.format("rotate({0} {1} {2})",
						this._.rt.deg, _, $);
				this.clip && G0(this.clip, {
							transform : k0.format("rotate({0} {1} {2})",
									-this._.rt.deg, _, $)
						})
			} else {
				this.transformations[0] = T0;
				this.clip && G0(this.clip, {
							transform : T0
						})
			}
			G0(this.node, {
						transform : this.transformations[a0](e0)
					});
			return this
		};
		Y0[O].hide = function() {
			!this.removed && (this.node.style.display = "none");
			return this
		};
		Y0[O].show = function() {
			!this.removed && (this.node.style.display = "");
			return this
		};
		Y0[O].remove = function() {
			if (this.removed)
				return;
			q0(this, this.paper);
			this.node.parentNode.removeChild(this.node);
			for (var $ in this)
				delete this[$];
			this.removed = true
		};
		Y0[O].getBBox = function() {
			if (this.removed)
				return this;
			if (this.type == "path")
				return Y(this.attrs.path);
			if (this.node.style.display == "none") {
				this.show();
				var $ = true
			}
			var D = {};
			try {
				D = this.node.getBBox()
			} catch (A) {
			} finally {
				D = D || {}
			}
			if (this.type == "text") {
				D = {
					x : D.x,
					y : Infinity,
					width : 0,
					height : 0
				};
				for (var _ = 0, B = this.node.getNumberOfChars(); _ < B; _++) {
					var C = this.node.getExtentOfChar(_);
					(C.y < D.y) && (D.y = C.y);
					(C.y + C.height - D.y > D.height)
							&& (D.height = C.y + C.height - D.y);
					(C.x + C.width - D.x > D.width)
							&& (D.width = C.x + C.width - D.x)
				}
			}
			$ && this.hide();
			return D
		};
		Y0[O].attr = function($, E) {
			if (this.removed)
				return this;
			if ($ == null) {
				var B = {};
				for (var C in this.attrs)
					if (this.attrs[Q](C))
						B[C] = this.attrs[C];
				this._.rt.deg && (B.rotation = this.rotate());
				(this._.sx != 1 || this._.sy != 1) && (B.scale = this.scale());
				B.gradient && B.fill == "none" && (B.fill = B.gradient)
						&& delete B.gradient;
				return B
			}
			if (E == null && k0.is($, "string")) {
				if ($ == "translation")
					return s.call(this);
				if ($ == "rotation")
					return this.rotate();
				if ($ == "scale")
					return this.scale();
				if ($ == "fill" && this.attrs.fill == "none"
						&& this.attrs.gradient)
					return this.attrs.gradient;
				return this.attrs[$]
			}
			if (E == null && k0.is($, "array")) {
				var _ = {};
				for (var A = 0, D = $.length; A < D; A++)
					_[$[A]] = this.attr($[A]);
				return _
			}
			if (E != null) {
				var F = {};
				F[$] = E;
				z0(this, F)
			} else if ($ != null && k0.is($, "object"))
				z0(this, $);
			return this
		};
		Y0[O].toFront = function() {
			if (this.removed)
				return this;
			this.node.parentNode[I0](this.node);
			var $ = this.paper;
			$.top != this && t0(this, $);
			return this
		};
		Y0[O].toBack = function() {
			if (this.removed)
				return this;
			if (this.node.parentNode.firstChild != this.node) {
				this.node.parentNode.insertBefore(this.node,
						this.node.parentNode.firstChild);
				m(this, this.paper);
				var $ = this.paper
			}
			return this
		};
		Y0[O].insertAfter = function(_) {
			if (this.removed)
				return this;
			var A = _.node;
			if (A.nextSibling)
				A.parentNode.insertBefore(this.node, A.nextSibling);
			else
				A.parentNode[I0](this.node);
			$(this, _, this.paper);
			return this
		};
		Y0[O].insertBefore = function($) {
			if (this.removed)
				return this;
			var _ = $.node;
			_.parentNode.insertBefore(this.node, _);
			h0(this, $, this.paper);
			return this
		};
		Y0[O].blur = function(A) {
			var _ = this;
			if (+A !== 0) {
				var B = G0("filter"), $ = G0("feGaussianBlur");
				_.attrs.blur = A;
				B.id = "r" + (k0._id++)[M0](36);
				G0($, {
							stdDeviation : +A || 1.5
						});
				B.appendChild($);
				_.paper.defs.appendChild(B);
				_._blur = B;
				G0(_.node, {
							filter : "url(#" + B.id + ")"
						})
			} else {
				if (_._blur) {
					_._blur.parentNode.removeChild(_._blur);
					delete _._blur;
					delete _.attrs.blur
				}
				_.node.removeAttribute("filter")
			}
		};
		var R = function(A, _, B, C) {
			_ = N(_);
			B = N(B);
			var $ = G0("circle");
			A.canvas && A.canvas[I0]($);
			var D = new Y0($, A);
			D.attrs = {
				cx : _,
				cy : B,
				r : C,
				fill : "none",
				stroke : "#000"
			};
			D.type = "circle";
			G0($, D.attrs);
			return D
		}, R0 = function(F, _, E, A, B, D) {
			_ = N(_);
			E = N(E);
			var C = G0("rect");
			F.canvas && F.canvas[I0](C);
			var $ = new Y0(C, F);
			$.attrs = {
				x : _,
				y : E,
				width : A,
				height : B,
				r : D || 0,
				rx : D || 0,
				ry : D || 0,
				fill : "none",
				stroke : "#000"
			};
			$.type = "rect";
			G0(C, $.attrs);
			return $
		}, r0 = function(A, _, D, B, C) {
			_ = N(_);
			D = N(D);
			var $ = G0("ellipse");
			A.canvas && A.canvas[I0]($);
			var E = new Y0($, A);
			E.attrs = {
				cx : _,
				cy : D,
				rx : B,
				ry : C,
				fill : "none",
				stroke : "#000"
			};
			E.type = "ellipse";
			G0($, E.attrs);
			return E
		}, x = function(F, D, _, E, A, B) {
			var C = G0("image");
			G0(C, {
						x : _,
						y : E,
						width : A,
						height : B,
						preserveAspectRatio : "none"
					});
			C.setAttributeNS(F.xlink, "href", D);
			F.canvas && F.canvas[I0](C);
			var $ = new Y0(C, F);
			$.attrs = {
				x : _,
				y : E,
				width : A,
				height : B,
				src : D
			};
			$.type = "image";
			return $
		}, w0 = function(A, _, B, C) {
			var $ = G0("text");
			G0($, {
						x : _,
						y : B,
						"text-anchor" : "middle"
					});
			A.canvas && A.canvas[I0]($);
			var D = new Y0($, A);
			D.attrs = {
				x : _,
				y : B,
				"text-anchor" : "middle",
				text : C,
				font : p.font,
				stroke : "none",
				fill : "#000"
			};
			D.type = "text";
			z0(D, D.attrs);
			return D
		}, b = function(_, $) {
			this.width = _ || this.width;
			this.height = $ || this.height;
			this.canvas[$0]("width", this.width);
			this.canvas[$0]("height", this.height);
			return this
		}, z = function() {
			var $ = j0[c](0, arguments), E = $ && $.container, A = $.x, D = $.y, C = $.width, _ = $.height;
			if (!E)
				throw new Error("SVG container not found.");
			var B = G0("svg");
			C = C || 512;
			_ = _ || 342;
			G0(B, {
						xmlns : "http://www.w3.org/2000/svg",
						version : 1.1,
						width : C,
						height : _
					});
			if (E == 1) {
				B.style.cssText = "position:absolute;left:" + A + "px;top:" + D
						+ "px";
				T.body[I0](B)
			} else if (E.firstChild)
				E.insertBefore(B, E.firstChild);
			else
				E[I0](B);
			E = new X;
			E.width = C;
			E.height = _;
			E.canvas = B;
			S0.call(E, E, k0.fn);
			E.clear();
			return E
		};
		X[O].clear = function() {
			var $ = this.canvas;
			while ($.firstChild)
				$.removeChild($.firstChild);
			this.bottom = this.top = null;
			(this.desc = G0("desc"))[I0](T
					.createTextNode("Created with Rapha\xebl"));
			$[I0](this.desc);
			$[I0](this.defs = G0("defs"))
		};
		X[O].remove = function() {
			this.canvas.parentNode
					&& this.canvas.parentNode.removeChild(this.canvas);
			for (var $ in this)
				this[$] = t($)
		}
	}
	if (k0.vml) {
		var J = {
			M : "m",
			L : "l",
			C : "c",
			Z : "x",
			m : "t",
			l : "r",
			c : "v",
			z : "x"
		}, c0 = /([clmz]),?([^clmz]*)/gi, d0 = /-?[^,\s-]+/g, L0 = 1000 + e0
				+ 1000, y = 10, E0 = function(G) {
			var K = /[ahqstv]/ig, $ = u;
			(G + T0).match(K) && ($ = F);
			K = /[clmz]/g;
			if ($ == u && !(G + T0).match(K)) {
				var I = (G + T0)[B0](c0, function(_, B, C) {
							var A = [], D = A0.call(B) == "m", $ = J[B];
							C[B0](d0, function(_) {
										if (D && A[k] == 2) {
											$ += A + J[B == "m" ? "l" : "L"];
											A = []
										}
										A[f](N(_ * y))
									});
							return $ + A
						});
				return I
			}
			var D = $(G), A, _;
			I = [];
			for (var B = 0, E = D[k]; B < E; B++) {
				A = D[B];
				_ = A0.call(D[B][0]);
				_ == "z" && (_ = "x");
				for (var C = 1, H = A[k]; C < H; C++)
					_ += N(A[C] * y) + (C != H - 1 ? "," : T0);
				I[f](_)
			}
			return I[a0](e0)
		};
		k0[M0] = function() {
			return "Your browser doesn\u2019t support SVG. Falling down to VML.\nYou are running Rapha\xebl "
					+ this.version
		};
		v = function(E, A) {
			var B = m0("group");
			B.style.cssText = "position:absolute;left:0;top:0;width:" + A.width
					+ "px;height:" + A.height + "px";
			B.coordsize = A.coordsize;
			B.coordorigin = A.coordorigin;
			var C = m0("shape"), $ = C.style;
			$.width = A.width + "px";
			$.height = A.height + "px";
			C.coordsize = L0;
			C.coordorigin = A.coordorigin;
			B[I0](C);
			var D = new Y0(C, B, A), _ = {
				fill : "none",
				stroke : "#000"
			};
			E && (_.path = E);
			D.isAbsolute = true;
			D.type = "path";
			D.path = [];
			D.Path = T0;
			z0(D, _);
			A.canvas[I0](B);
			return D
		};
		z0 = function(M, H) {
			M.attrs = M.attrs || {};
			var R = M.node, I = M.attrs, S = R.style, $, F = M;
			for (var U in H)
				if (H[Q](U))
					I[U] = H[U];
			H.href && (R.href = H.href);
			H.title && (R.title = H.title);
			H.target && (R.target = H.target);
			H.cursor && (S.cursor = H.cursor);
			"blur" in H && M.blur(H.blur);
			if (H.path && M.type == "path") {
				I.path = H.path;
				R.path = E0(I.path)
			}
			if (H.rotation != null)
				M.rotate(H.rotation, true);
			if (H.translation) {
				$ = (H.translation + T0)[E](j);
				s.call(M, $[0], $[1]);
				if (M._.rt.cx != null) {
					M._.rt.cx += +$[0];
					M._.rt.cy += +$[1];
					M.setBox(M.attrs, $[0], $[1])
				}
			}
			if (H.scale) {
				$ = (H.scale + T0)[E](j);
				M.scale(+$[0] || 1, +$[1] || +$[0] || 1, +$[2] || null, +$[3]
								|| null)
			}
			if ("clip-rect" in H) {
				var _ = (H["clip-rect"] + T0)[E](j);
				if (_[k] == 4) {
					_[2] = +_[2] + (+_[0]);
					_[3] = +_[3] + (+_[1]);
					var V = R.clipRect || T.createElement("div"), D = V.style, J = R.parentNode;
					D.clip = k0.format("rect({1}px {2}px {3}px {0}px)", _);
					if (!R.clipRect) {
						D.position = "absolute";
						D.top = 0;
						D.left = 0;
						D.width = M.paper.width + "px";
						D.height = M.paper.height + "px";
						J.parentNode.insertBefore(V, J);
						V[I0](J);
						R.clipRect = V
					}
				}
				if (!H["clip-rect"])
					R.clipRect && (R.clipRect.style.clip = T0)
			}
			if (M.type == "image" && H.src)
				R.src = H.src;
			if (M.type == "image" && H.opacity) {
				R.filterOpacity = " progid:DXImageTransform.Microsoft.Alpha(opacity="
						+ (H.opacity * 100) + ")";
				S.filter = (R.filterMatrix || T0) + (R.filterOpacity || T0)
			}
			H.font && (S.font = H.font);
			H["font-family"]
					&& (S.fontFamily = "\""
							+ H["font-family"][E](",")[0][B0](/^['"]+|['"]+$/g,
									T0) + "\"");
			H["font-size"] && (S.fontSize = H["font-size"]);
			H["font-weight"] && (S.fontWeight = H["font-weight"]);
			H["font-style"] && (S.fontStyle = H["font-style"]);
			if (H.opacity != null || H["stroke-width"] != null
					|| H.fill != null || H.stroke != null
					|| H["stroke-width"] != null || H["stroke-opacity"] != null
					|| H["fill-opacity"] != null
					|| H["stroke-dasharray"] != null
					|| H["stroke-miterlimit"] != null
					|| H["stroke-linejoin"] != null
					|| H["stroke-linecap"] != null) {
				R = M.shape || R;
				var G = (R.getElementsByTagName("fill") && R
						.getElementsByTagName("fill")[0]), L = false;
				!G && (L = G = m0("fill"));
				if ("fill-opacity" in H || "opacity" in H) {
					var A = ((+I["fill-opacity"] + 1 || 2) - 1)
							* ((+I.opacity + 1 || 2) - 1);
					A < 0 && (A = 0);
					A > 1 && (A = 1);
					G.opacity = A
				}
				H.fill && (G.on = true);
				if (G.on == null || H.fill == "none")
					G.on = false;
				if (G.on && H.fill) {
					var B = H.fill.match(i);
					if (B) {
						G.src = B[1];
						G.type = "tile"
					} else {
						G.color = k0.getRGB(H.fill).hex;
						G.src = T0;
						G.type = "solid";
						if (k0.getRGB(H.fill).error && (F.type in {
							circle : 1,
							ellipse : 1
						} || (H.fill + T0).charAt() != "r") && h(F, H.fill)) {
							I.fill = "none";
							I.gradient = H.fill
						}
					}
				}
				L && R[I0](G);
				var K = (R.getElementsByTagName("stroke") && R
						.getElementsByTagName("stroke")[0]), C = false;
				!K && (C = K = m0("stroke"));
				if ((H.stroke && H.stroke != "none") || H["stroke-width"]
						|| H["stroke-opacity"] != null || H["stroke-dasharray"]
						|| H["stroke-miterlimit"] || H["stroke-linejoin"]
						|| H["stroke-linecap"])
					K.on = true;
				(H.stroke == "none" || K.on == null || H.stroke == 0 || H["stroke-width"] == 0)
						&& (K.on = false);
				K.on && H.stroke && (K.color = k0.getRGB(H.stroke).hex);
				A = ((+I["stroke-opacity"] + 1 || 2) - 1)
						* ((+I.opacity + 1 || 2) - 1);
				var O = (v0(H["stroke-width"]) || 1) * 0.75;
				A < 0 && (A = 0);
				A > 1 && (A = 1);
				H["stroke-width"] == null && (O = I["stroke-width"]);
				H["stroke-width"] && (K.weight = O);
				O && O < 1 && (A *= O) && (K.weight = 1);
				K.opacity = A;
				H["stroke-linejoin"]
						&& (K.joinstyle = H["stroke-linejoin"] || "miter");
				K.miterlimit = H["stroke-miterlimit"] || 8;
				H["stroke-linecap"]
						&& (K.endcap = H["stroke-linecap"] == "butt"
								? "flat"
								: H["stroke-linecap"] == "square"
										? "square"
										: "round");
				if (H["stroke-dasharray"]) {
					var P = {
						"-" : "shortdash",
						"." : "shortdot",
						"-." : "shortdashdot",
						"-.." : "shortdashdotdot",
						". " : "dot",
						"- " : "dash",
						"--" : "longdash",
						"- ." : "dashdot",
						"--." : "longdashdot",
						"--.." : "longdashdotdot"
					};
					K.dashstyle = P[Q](H["stroke-dasharray"])
							? P[H["stroke-dasharray"]]
							: T0
				}
				C && R[I0](K)
			}
			if (F.type == "text") {
				S = F.paper.span.style;
				I.font && (S.font = I.font);
				I["font-family"] && (S.fontFamily = I["font-family"]);
				I["font-size"] && (S.fontSize = I["font-size"]);
				I["font-weight"] && (S.fontWeight = I["font-weight"]);
				I["font-style"] && (S.fontStyle = I["font-style"]);
				F.node.string
						&& (F.paper.span.innerHTML = (F.node.string + T0)[B0](
								/</g, "&#60;")[B0](/&/g, "&#38;")[B0](/\n/g,
								"<br>"));
				F.W = I.w = F.paper.span.offsetWidth;
				F.H = I.h = F.paper.span.offsetHeight;
				F.X = I.x;
				F.Y = I.y + N(F.H / 2);
				switch (I["text-anchor"]) {
					case "start" :
						F.node.style["v-text-align"] = "left";
						F.bbx = N(F.W / 2);
						break;
					case "end" :
						F.node.style["v-text-align"] = "right";
						F.bbx = -N(F.W / 2);
						break;
					default :
						F.node.style["v-text-align"] = "center";
						break
				}
			}
		};
		h = function(_, I) {
			_.attrs = _.attrs || {};
			var J = _.attrs, F = _.node.getElementsByTagName("fill"), B = "linear", H = ".5 .5";
			_.attrs.gradient = I;
			I = (I + T0)[B0](b0, function(_, $, A) {
						B = "radial";
						if ($ && A) {
							$ = v0($);
							A = v0(A);
							H0($ - 0.5, 2) + H0(A - 0.5, 2) > 0.25
									&& (A = $1.sqrt(0.25 - H0($ - 0.5, 2))
											* ((A > 0.5) * 2 - 1) + 0.5);
							H = $ + e0 + A
						}
						return T0
					});
			I = I[E](/\s*\-\s*/);
			if (B == "linear") {
				var A = I.shift();
				A = -v0(A);
				if (isNaN(A))
					return null
			}
			var C = w(I);
			if (!C)
				return null;
			_ = _.shape || _.node;
			F = F[0] || m0("fill");
			if (C[k]) {
				F.on = true;
				F.method = "none";
				F.type = (B == "radial") ? "gradientradial" : "gradient";
				F.color = C[0].color;
				F.color2 = C[C[k] - 1].color;
				var G = [];
				for (var $ = 0, D = C[k]; $ < D; $++)
					C[$].offset && G[f](C[$].offset + e0 + C[$].color);
				F.colors
						&& (F.colors.value = G[k] ? G[a0](",") : "0% "
								+ F.color);
				if (B == "radial") {
					F.focus = "100%";
					F.focussize = H;
					F.focusposition = H
				} else
					F.angle = (270 - A) % 360
			}
			return 1
		};
		Y0 = function(C, D, _) {
			var B = 0, E = 0, A = 0, $ = 1;
			this[0] = C;
			this.id = k0._oid++;
			this.node = C;
			C.raphael = this;
			this.X = 0;
			this.Y = 0;
			this.attrs = {};
			this.Group = D;
			this.paper = _;
			this._ = {
				tx : 0,
				ty : 0,
				rt : {
					deg : 0
				},
				sx : 1,
				sy : 1
			};
			!_.bottom && (_.bottom = this);
			this.prev = _.top;
			_.top && (_.top.next = this);
			_.top = this;
			this.next = null
		};
		Y0[O].rotate = function(_, $, A) {
			if (this.removed)
				return this;
			if (_ == null) {
				if (this._.rt.cx)
					return [this._.rt.deg, this._.rt.cx, this._.rt.cy][a0](e0);
				return this._.rt.deg
			}
			_ = (_ + T0)[E](j);
			if (_[k] - 1) {
				$ = v0(_[1]);
				A = v0(_[2])
			}
			_ = v0(_[0]);
			if ($ != null)
				this._.rt.deg = _;
			else
				this._.rt.deg += _;
			A == null && ($ = null);
			this._.rt.cx = $;
			this._.rt.cy = A;
			this.setBox(this.attrs, $, A);
			this.Group.style.rotation = this._.rt.deg;
			return this
		};
		Y0[O].setBox = function(C, A, _) {
			if (this.removed)
				return this;
			var O = this.Group.style, I = (this.shape && this.shape.style)
					|| this.node.style;
			C = C || {};
			for (var J in C)
				if (C[Q](J))
					this.attrs[J] = C[J];
			A = A || this._.rt.cx;
			_ = _ || this._.rt.cy;
			var F = this.attrs, R, P, S, B;
			switch (this.type) {
				case "circle" :
					R = F.cx - F.r;
					P = F.cy - F.r;
					S = B = F.r * 2;
					break;
				case "ellipse" :
					R = F.cx - F.rx;
					P = F.cy - F.ry;
					S = F.rx * 2;
					B = F.ry * 2;
					break;
				case "rect" :
				case "image" :
					R = +F.x;
					P = +F.y;
					S = F.width || 0;
					B = F.height || 0;
					break;
				case "text" :
					this.textpath.v = ["m", N(F.x), ", ", N(F.y - 2), "l",
							N(F.x) + 1, ", ", N(F.y - 2)][a0](T0);
					R = F.x - N(this.W / 2);
					P = F.y - this.H / 2;
					S = this.W;
					B = this.H;
					break;
				case "path" :
					if (!this.attrs.path) {
						R = 0;
						P = 0;
						S = this.paper.width;
						B = this.paper.height
					} else {
						var H = Y(this.attrs.path);
						R = H.x;
						P = H.y;
						S = H.width;
						B = H.height
					}
					break;
				default :
					R = 0;
					P = 0;
					S = this.paper.width;
					B = this.paper.height;
					break
			}
			A = (A == null) ? R + S / 2 : A;
			_ = (_ == null) ? P + B / 2 : _;
			var $ = A - this.paper.width / 2, M = _ - this.paper.height / 2, L;
			O.left != (L = $ + "px") && (O.left = L);
			O.top != (L = M + "px") && (O.top = L);
			this.X = this.type == "path" ? -$ : R;
			this.Y = this.type == "path" ? -M : P;
			this.W = S;
			this.H = B;
			if (this.type == "path") {
				I.left != (L = -$ * y + "px") && (I.left = L);
				I.top != (L = -M * y + "px") && (I.top = L)
			} else if (this.type == "text") {
				I.left != (L = -$ + "px") && (I.left = L);
				I.top != (L = -M + "px") && (I.top = L)
			} else {
				O.width != (L = this.paper.width + "px") && (O.width = L);
				O.height != (L = this.paper.height + "px") && (O.height = L);
				I.left != (L = R - $ + "px") && (I.left = L);
				I.top != (L = P - M + "px") && (I.top = L);
				I.width != (L = S + "px") && (I.width = L);
				I.height != (L = B + "px") && (I.height = L);
				var G = (+C.r || 0) / D0(S, B);
				if (this.type == "rect"
						&& this.arcsize.toFixed(4) != G.toFixed(4)
						&& (G || this.arcsize)) {
					var E = m0("roundrect"), D = {}, K = this.events
							&& this.events[k];
					J = 0;
					E.arcsize = G;
					E.raphael = this;
					this.Group[I0](E);
					this.Group.removeChild(this.node);
					this[0] = this.node = E;
					this.arcsize = G;
					for (J in F)
						D[J] = F[J];
					delete D.scale;
					this.attr(D);
					if (this.events)
						for (; J < K; J++)
							this.events[J].unbind = n0(this.node,
									this.events[J].name, this.events[J].f, this)
				}
			}
		};
		Y0[O].hide = function() {
			!this.removed && (this.Group.style.display = "none");
			return this
		};
		Y0[O].show = function() {
			!this.removed && (this.Group.style.display = "block");
			return this
		};
		Y0[O].getBBox = function() {
			if (this.removed)
				return this;
			if (this.type == "path")
				return Y(this.attrs.path);
			return {
				x : this.X + (this.bbx || 0),
				y : this.Y,
				width : this.W,
				height : this.H
			}
		};
		Y0[O].remove = function() {
			if (this.removed)
				return;
			q0(this, this.paper);
			this.node.parentNode.removeChild(this.node);
			this.Group.parentNode.removeChild(this.Group);
			this.shape && this.shape.parentNode.removeChild(this.shape);
			for (var $ in this)
				delete this[$];
			this.removed = true
		};
		Y0[O].attr = function(A, D) {
			if (this.removed)
				return this;
			if (A == null) {
				var C = {};
				for (var $ in this.attrs)
					if (this.attrs[Q]($))
						C[$] = this.attrs[$];
				this._.rt.deg && (C.rotation = this.rotate());
				(this._.sx != 1 || this._.sy != 1) && (C.scale = this.scale());
				C.gradient && C.fill == "none" && (C.fill = C.gradient)
						&& delete C.gradient;
				return C
			}
			if (D == null && k0.is(A, "string")) {
				if (A == "translation")
					return s.call(this);
				if (A == "rotation")
					return this.rotate();
				if (A == "scale")
					return this.scale();
				if (A == "fill" && this.attrs.fill == "none"
						&& this.attrs.gradient)
					return this.attrs.gradient;
				return this.attrs[A]
			}
			if (this.attrs && D == null && k0.is(A, "array")) {
				var B, _ = {};
				for ($ = 0, B = A[k]; $ < B; $++)
					_[A[$]] = this.attr(A[$]);
				return _
			}
			var E;
			if (D != null) {
				E = {};
				E[A] = D
			}
			D == null && k0.is(A, "object") && (E = A);
			if (E) {
				if (E.text && this.type == "text")
					this.node.string = E.text;
				z0(this, E);
				if (E.gradient && (({
					circle : 1,
					ellipse : 1
				})[Q](this.type) || (E.gradient + T0).charAt() != "r"))
					h(this, E.gradient);
				(this.type != "path" || this._.rt.deg)
						&& this.setBox(this.attrs)
			}
			return this
		};
		Y0[O].toFront = function() {
			!this.removed && this.Group.parentNode[I0](this.Group);
			this.paper.top != this && t0(this, this.paper);
			return this
		};
		Y0[O].toBack = function() {
			if (this.removed)
				return this;
			if (this.Group.parentNode.firstChild != this.Group) {
				this.Group.parentNode.insertBefore(this.Group,
						this.Group.parentNode.firstChild);
				m(this, this.paper)
			}
			return this
		};
		Y0[O].insertAfter = function(_) {
			if (this.removed)
				return this;
			if (_.Group.nextSibling)
				_.Group.parentNode
						.insertBefore(this.Group, _.Group.nextSibling);
			else
				_.Group.parentNode[I0](this.Group);
			$(this, _, this.paper);
			return this
		};
		Y0[O].insertBefore = function($) {
			if (this.removed)
				return this;
			$.Group.parentNode.insertBefore(this.Group, $.Group);
			h0(this, $, this.paper);
			return this
		};
		var W = / progid:\S+Blur\([^\)]+\)/g;
		Y0[O].blur = function($) {
			var _ = this.node.style, A = _.filter;
			A = A.replace(W, "");
			if (+$ !== 0) {
				this.attrs.blur = $;
				_.filter = A
						+ " progid:DXImageTransform.Microsoft.Blur(pixelradius="
						+ (+$ || 1.5) + ")";
				_.margin = Raphael.format("-{0}px 0 0 -{0}px", Math.round(+$
								|| 1.5))
			} else {
				_.filter = A;
				_.margin = 0;
				delete this.attrs.blur
			}
		};
		R = function(A, _, E, B) {
			var C = m0("group"), D = m0("oval"), F = D.style;
			C.style.cssText = "position:absolute;left:0;top:0;width:" + A.width
					+ "px;height:" + A.height + "px";
			C.coordsize = L0;
			C.coordorigin = A.coordorigin;
			C[I0](D);
			var $ = new Y0(D, C, A);
			$.type = "circle";
			z0($, {
						stroke : "#000",
						fill : "none"
					});
			$.attrs.cx = _;
			$.attrs.cy = E;
			$.attrs.r = B;
			$.setBox({
						x : _ - B,
						y : E - B,
						width : B * 2,
						height : B * 2
					});
			A.canvas[I0](C);
			return $
		};
		R0 = function(A, F, E, H, $, _) {
			var C = m0("group"), G = m0("roundrect"), D = (+_ || 0)
					/ (D0(H, $));
			C.style.cssText = "position:absolute;left:0;top:0;width:" + A.width
					+ "px;height:" + A.height + "px";
			C.coordsize = L0;
			C.coordorigin = A.coordorigin;
			C[I0](G);
			G.arcsize = D;
			var B = new Y0(G, C, A);
			B.type = "rect";
			z0(B, {
						stroke : "#000"
					});
			B.arcsize = D;
			B.setBox({
						x : F,
						y : E,
						width : H,
						height : $,
						r : _
					});
			A.canvas[I0](C);
			return B
		};
		r0 = function(_, G, E, F, A) {
			var C = m0("group"), $ = m0("oval"), D = $.style;
			C.style.cssText = "position:absolute;left:0;top:0;width:" + _.width
					+ "px;height:" + _.height + "px";
			C.coordsize = L0;
			C.coordorigin = _.coordorigin;
			C[I0]($);
			var B = new Y0($, C, _);
			B.type = "ellipse";
			z0(B, {
						stroke : "#000"
					});
			B.attrs.cx = G;
			B.attrs.cy = E;
			B.attrs.rx = F;
			B.attrs.ry = A;
			B.setBox({
						x : G - F,
						y : E - A,
						width : F * 2,
						height : A * 2
					});
			_.canvas[I0](C);
			return B
		};
		x = function(A, _, H, F, D, $) {
			var C = m0("group"), G = m0("image"), E = G.style;
			C.style.cssText = "position:absolute;left:0;top:0;width:" + A.width
					+ "px;height:" + A.height + "px";
			C.coordsize = L0;
			C.coordorigin = A.coordorigin;
			G.src = _;
			C[I0](G);
			var B = new Y0(G, C, A);
			B.type = "image";
			B.attrs.src = _;
			B.attrs.x = H;
			B.attrs.y = F;
			B.attrs.w = D;
			B.attrs.h = $;
			B.setBox({
						x : H,
						y : F,
						width : D,
						height : $
					});
			A.canvas[I0](C);
			return B
		};
		w0 = function(A, I, G, D) {
			var C = m0("group"), $ = m0("shape"), F = $.style, E = m0("path"), _ = E.style, H = m0("textpath");
			C.style.cssText = "position:absolute;left:0;top:0;width:" + A.width
					+ "px;height:" + A.height + "px";
			C.coordsize = L0;
			C.coordorigin = A.coordorigin;
			E.v = k0.format("m{0},{1}l{2},{1}", N(I * 10), N(G * 10), N(I * 10)
							+ 1);
			E.textpathok = true;
			F.width = A.width;
			F.height = A.height;
			H.string = D + T0;
			H.on = true;
			$[I0](H);
			$[I0](E);
			C[I0]($);
			var B = new Y0(H, C, A);
			B.shape = $;
			B.textpath = E;
			B.type = "text";
			B.attrs.text = D;
			B.attrs.x = I;
			B.attrs.y = G;
			B.attrs.w = 1;
			B.attrs.h = 1;
			z0(B, {
						font : p.font,
						stroke : "none",
						fill : "#000"
					});
			B.setBox();
			A.canvas[I0](C);
			return B
		};
		b = function(A, $) {
			var _ = this.canvas.style;
			A == +A && (A += "px");
			$ == +$ && ($ += "px");
			_.width = A;
			_.height = $;
			_.clip = "rect(0 " + A + " " + $ + " 0)";
			return this
		};
		var m0;
		T.createStyleSheet().addRule(".rvml", "behavior:url(#default#VML)");
		try {
			!T.namespaces.rvml
					&& T.namespaces
							.add("rvml", "urn:schemas-microsoft-com:vml");
			m0 = function($) {
				return T.createElement("<rvml:" + $ + " class=\"rvml\">")
			}
		} catch (o0) {
			m0 = function($) {
				return T
						.createElement("<"
								+ $
								+ " xmlns=\"urn:schemas-microsoft.com:vml\" class=\"rvml\">")
			}
		}
		z = function() {
			var H = j0[c](0, arguments), _ = H.container, G = H.height, D, A = H.width, F = H.x, E = H.y;
			if (!_)
				throw new Error("VML container not found.");
			var C = new X, B = C.canvas = T.createElement("div"), $ = B.style;
			A = A || 512;
			G = G || 342;
			A == +A && (A += "px");
			G == +G && (G += "px");
			C.width = 1000;
			C.height = 1000;
			C.coordsize = y * 1000 + e0 + y * 1000;
			C.coordorigin = "0 0";
			C.span = T.createElement("span");
			C.span.style.cssText = "position:absolute;left:-9999em;top:-9999em;padding:0;margin:0;line-height:1;display:inline;";
			B[I0](C.span);
			$.cssText = k0
					.format(
							"width:{0};height:{1};position:absolute;clip:rect(0 {0} {1} 0);overflow:hidden",
							A, G);
			if (_ == 1) {
				T.body[I0](B);
				$.left = F + "px";
				$.top = E + "px"
			} else {
				_.style.width = A;
				_.style.height = G;
				if (_.firstChild)
					_.insertBefore(B, _.firstChild);
				else
					_[I0](B)
			}
			S0.call(C, C, k0.fn);
			return C
		};
		X[O].clear = function() {
			this.canvas.innerHTML = T0;
			this.span = T.createElement("span");
			this.span.style.cssText = "position:absolute;left:-9999em;top:-9999em;padding:0;margin:0;line-height:1;display:inline;";
			this.canvas[I0](this.span);
			this.bottom = this.top = null
		};
		X[O].remove = function() {
			this.canvas.parentNode.removeChild(this.canvas);
			for (var $ in this)
				this[$] = t($);
			return true
		}
	}
	if ((/^Apple|^Google/).test(U0.navigator.vendor)
			&& !(U0.navigator.userAgent.indexOf("Version/4.0") + 1))
		X[O].safari = function() {
			var $ = this.rect(-99, -99, this.width + 99, this.height + 99);
			U0.setTimeout(function() {
						$.remove()
					})
		};
	else
		X[O].safari = function() {
		};
	var n0 = (function() {
		if (T.addEventListener)
			return function(B, C, A, _) {
				var $ = function($) {
					return A.call(_, $)
				};
				B.addEventListener(C, $, false);
				return function() {
					B.removeEventListener(C, $, false);
					return true
				}
			};
		else if (T.attachEvent)
			return function(B, $, D, A) {
				var C = function($) {
					return D.call(A, $ || U0.event)
				};
				B.attachEvent("on" + $, C);
				var _ = function() {
					B.detachEvent("on" + $, C);
					return true
				};
				return _
			}
	})();
	for (var x0 = L[k]; x0--;)
		(function($) {
			Y0[O][$] = function(_) {
				if (k0.is(_, "function")) {
					this.events = this.events || [];
					this.events.push({
								name : $,
								f : _,
								unbind : n0(this.shape || this.node, $, _, this)
							})
				}
				return this
			};
			Y0[O]["un" + $] = function(_) {
				var B = this.events, A = B[k];
				while (A--)
					if (B[A].name == $ && B[A].f == _) {
						B[A].unbind();
						B.splice(A, 1);
						!B.length && delete this.events;
						return this
					}
				return this
			}
		})(L[x0]);
	Y0[O].hover = function(_, $) {
		return this.mouseover(_).mouseout($)
	};
	Y0[O].unhover = function(_, $) {
		return this.unmouseover(_).unmouseout($)
	};
	X[O].circle = function($, A, _) {
		return R(this, $ || 0, A || 0, _ || 0)
	};
	X[O].rect = function(_, B, A, C, $) {
		return R0(this, _ || 0, B || 0, A || 0, C || 0, $ || 0)
	};
	X[O].ellipse = function(_, $, B, A) {
		return r0(this, _ || 0, $ || 0, B || 0, A || 0)
	};
	X[O].path = function($) {
		$ && !k0.is($, "string") && !k0.is($[0], "array") && ($ += T0);
		return v(k0.format[c](k0, arguments), this)
	};
	X[O].image = function($, _, B, A, C) {
		return x(this, $ || "about:blank", _ || 0, B || 0, A || 0, C || 0)
	};
	X[O].text = function($, A, _) {
		return w0(this, $ || 0, A || 0, _ || T0)
	};
	X[O].set = function($) {
		arguments[k] > 1
				&& ($ = Array[O].splice.call(arguments, 0, arguments[k]));
		return new a($)
	};
	X[O].setSize = b;
	X[O].top = X[O].bottom = null;
	X[O].raphael = k0;
	function _0() {
		return this.x + e0 + this.y
	}
	Y0[O].scale = function(X, W, $, d) {
		if (X == null && W == null)
			return {
				x : this._.sx,
				y : this._.sy,
				toString : _0
			};
		W = W || X;
		!+W && (W = X);
		var S, O, Q, N, D = this.attrs;
		if (X != 0) {
			var U = this.getBBox(), a = U.x + U.width / 2, R = U.y + U.height
					/ 2, E = X / this._.sx, B = W / this._.sy;
			$ = (+$ || $ == 0) ? $ : a;
			d = (+d || d == 0) ? d : R;
			var T = ~~(X / $1.abs(X)), Z = ~~(W / $1.abs(W)), L = this.node.style, A = $
					+ (a - $) * E, _ = d + (R - d) * B;
			switch (this.type) {
				case "rect" :
				case "image" :
					var b = D.width * T * E, K = D.height * Z * B;
					this.attr({
								height : K,
								r : D.r * D0(T * E, Z * B),
								width : b,
								x : A - b / 2,
								y : _ - K / 2
							});
					break;
				case "circle" :
				case "ellipse" :
					this.attr({
								rx : D.rx * T * E,
								ry : D.ry * Z * B,
								r : D.r * D0(T * E, Z * B),
								cx : A,
								cy : _
							});
					break;
				case "path" :
					var G = y0(D.path), F = true;
					for (var H = 0, J = G[k]; H < J; H++) {
						var M = G[H], P = J0.call(M[0]);
						if (P == "M" && F)
							continue;
						else
							F = false;
						if (P == "A") {
							M[G[H][k] - 2] *= E;
							M[G[H][k] - 1] *= B;
							M[1] *= T * E;
							M[2] *= Z * B;
							M[5] = +!(T + Z ? !+M[5] : +M[5])
						} else if (P == "H") {
							for (var I = 1, C = M[k]; I < C; I++)
								M[I] *= E
						} else if (P == "V") {
							for (I = 1, C = M[k]; I < C; I++)
								M[I] *= B
						} else
							for (I = 1, C = M[k]; I < C; I++)
								M[I] *= (I % 2) ? E : B
					}
					var c = Y(G);
					S = A - c.x - c.width / 2;
					O = _ - c.y - c.height / 2;
					G[0][1] += S;
					G[0][2] += O;
					this.attr({
								path : G
							});
					break
			}
			if (this.type in {
				text : 1,
				image : 1
			} && (T != 1 || Z != 1)) {
				if (this.transformations) {
					this.transformations[2] = "scale("[V](T, ",", Z, ")");
					this.node[$0]("transform", this.transformations[a0](e0));
					S = (T == -1) ? -D.x - (b || 0) : D.x;
					O = (Z == -1) ? -D.y - (K || 0) : D.y;
					this.attr({
								x : S,
								y : O
							});
					D.fx = T - 1;
					D.fy = Z - 1
				} else {
					this.node.filterMatrix = " progid:DXImageTransform.Microsoft.Matrix(M11="[V](
							T, ", M12=0, M21=0, M22=", Z,
							", Dx=0, Dy=0, sizingmethod='auto expand', filtertype='bilinear')");
					L.filter = (this.node.filterMatrix || T0)
							+ (this.node.filterOpacity || T0)
				}
			} else if (this.transformations) {
				this.transformations[2] = T0;
				this.node[$0]("transform", this.transformations[a0](e0));
				D.fx = 0;
				D.fy = 0
			} else {
				this.node.filterMatrix = T0;
				L.filter = (this.node.filterMatrix || T0)
						+ (this.node.filterOpacity || T0)
			}
			D.scale = [X, W, $, d][a0](e0);
			this._.sx = X;
			this._.sy = W
		}
		return this
	};
	Y0[O].clone = function() {
		var $ = this.attr();
		delete $.scale;
		delete $.translation;
		return this.paper[this.type]().attr($)
	};
	var g = s0(function($, _, J, I, B, H, G, F, D) {
				var E = 0, C;
				for (var K = 0; K < 1.001; K += 0.001) {
					var A = k0.findDotsAtSegment($, _, J, I, B, H, G, F, K);
					K && (E += H0(H0(C.x - A.x, 2) + H0(C.y - A.y, 2), 0.5));
					if (E >= D)
						return A;
					C = A
				}
			}), N0 = function($, _) {
		return function(D, E, L) {
			D = F(D);
			var J, I, A, M, G = "", C = {}, K, H = 0;
			for (var N = 0, B = D.length; N < B; N++) {
				A = D[N];
				if (A[0] == "M") {
					J = +A[1];
					I = +A[2]
				} else {
					M = l(J, I, A[1], A[2], A[3], A[4], A[5], A[6]);
					if (H + M > E) {
						if (_ && !C.start) {
							K = g(J, I, A[1], A[2], A[3], A[4], A[5], A[6], E
											- H);
							G += ["C", K.start.x, K.start.y, K.m.x, K.m.y, K.x,
									K.y];
							if (L)
								return G;
							C.start = G;
							G = ["M", K.x, K.y + "C", K.n.x, K.n.y, K.end.x,
									K.end.y, A[5], A[6]][a0]();
							H += M;
							J = +A[5];
							I = +A[6];
							continue
						}
						if (!$ && !_) {
							K = g(J, I, A[1], A[2], A[3], A[4], A[5], A[6], E
											- H);
							return {
								x : K.x,
								y : K.y,
								alpha : K.alpha
							}
						}
					}
					H += M;
					J = +A[5];
					I = +A[6]
				}
				G += A
			}
			C.end = G;
			K = $ ? H : _ ? C : k0.findDotsAtSegment(J, I, A[1], A[2], A[3],
					A[4], A[5], A[6], 1);
			K.alpha && (K = {
				x : K.x,
				y : K.y,
				alpha : K.alpha
			});
			return K
		}
	}, l = s0(function($, _, H, B, G, F, E, D) {
				var C = {
					x : 0,
					y : 0
				}, J = 0;
				for (var I = 0; I < 1.01; I += 0.01) {
					var A = P($, _, H, B, G, F, E, D, I);
					I && (J += H0(H0(C.x - A.x, 2) + H0(C.y - A.y, 2), 0.5));
					C = A
				}
				return J
			}), i0 = N0(1), A = N0(), H = N0(0, 1);
	Y0[O].getTotalLength = function() {
		if (this.type != "path")
			return;
		return i0(this.attrs.path)
	};
	Y0[O].getPointAtLength = function($) {
		if (this.type != "path")
			return;
		return A(this.attrs.path, $)
	};
	Y0[O].getSubpath = function(A, _) {
		if (this.type != "path")
			return;
		if ($1.abs(this.getTotalLength() - _) < 0.000001)
			return H(this.attrs.path, A).end;
		var $ = H(this.attrs.path, _, 1);
		return A ? H($, A).end : $
	};
	k0.easing_formulas = {
		linear : function($) {
			return $
		},
		"<" : function($) {
			return H0($, 3)
		},
		">" : function($) {
			return H0($ - 1, 3) + 1
		},
		"<>" : function($) {
			$ = $ * 2;
			if ($ < 1)
				return H0($, 3) / 2;
			$ -= 2;
			return (H0($, 3) + 2) / 2
		},
		backIn : function(_) {
			var $ = 1.70158;
			return _ * _ * (($ + 1) * _ - $)
		},
		backOut : function(_) {
			_ = _ - 1;
			var $ = 1.70158;
			return _ * _ * (($ + 1) * _ + $) + 1
		},
		elastic : function(A) {
			if (A == 0 || A == 1)
				return A;
			var _ = 0.3, $ = _ / 4;
			return H0(2, -10 * A) * $1.sin((A - $) * (2 * $1.PI) / _) + 1
		},
		bounce : function($) {
			var A = 7.5625, B = 2.75, _;
			if ($ < (1 / B))
				_ = A * $ * $;
			else if ($ < (2 / B)) {
				$ -= (1.5 / B);
				_ = A * $ * $ + 0.75
			} else if ($ < (2.5 / B)) {
				$ -= (2.25 / B);
				_ = A * $ * $ + 0.9375
			} else {
				$ -= (2.625 / B);
				_ = A * $ * $ + 0.984375
			}
			return _
		}
	};
	var G = {
		length : 0
	}, U = function() {
		var a = +new Date;
		for (var K in G)
			if (K != "length" && G[Q](K)) {
				var B = G[K];
				if (B.stop || B.el.removed) {
					delete G[K];
					G[k]--;
					continue
				}
				var Y = a - B.start, H = B.ms, R = B.easing, D = B.from, L = B.diff, $ = B.to, X = B.t, O = B.prev
						|| 0, Z = B.el, S = B.callback, M = {}, b;
				if (Y < H) {
					var P = k0.easing_formulas[R]
							? k0.easing_formulas[R](Y / H)
							: Y / H;
					for (var I in D)
						if (D[Q](I)) {
							switch (u0[I]) {
								case "along" :
									b = P * H * L[I];
									$.back && (b = $.len - b);
									var J = A($[I], b);
									Z.translate(L.sx - L.x || 0, L.sy - L.y
													|| 0);
									L.x = J.x;
									L.y = J.y;
									Z.translate(J.x - L.sx, J.y - L.sy);
									$.rot && Z.rotate(L.r + J.alpha, J.x, J.y);
									break;
								case "number" :
									b = +D[I] + P * H * L[I];
									break;
								case "colour" :
									b = "rgb("
											+ [
													_(N(D[I].r + P * H * L[I].r)),
													_(N(D[I].g + P * H * L[I].g)),
													_(N(D[I].b + P * H * L[I].b))][a0](",")
											+ ")";
									break;
								case "path" :
									b = [];
									for (var F = 0, W = D[I][k]; F < W; F++) {
										b[F] = [D[I][F][0]];
										for (var C = 1, E = D[I][F][k]; C < E; C++)
											b[F][C] = +D[I][F][C] + P * H
													* L[I][F][C];
										b[F] = b[F][a0](e0)
									}
									b = b[a0](e0);
									break;
								case "csv" :
									switch (I) {
										case "translation" :
											var V = L[I][0] * (Y - O), T = L[I][1]
													* (Y - O);
											X.x += V;
											X.y += T;
											b = V + e0 + T;
											break;
										case "rotation" :
											b = +D[I][0] + P * H * L[I][0];
											D[I][1]
													&& (b += "," + D[I][1]
															+ "," + D[I][2]);
											break;
										case "scale" :
											b = [+D[I][0] + P * H * L[I][0],
													+D[I][1] + P * H * L[I][1],
													(2 in $[I] ? $[I][2] : T0),
													(3 in $[I] ? $[I][3] : T0)][a0](e0);
											break;
										case "clip-rect" :
											b = [];
											F = 4;
											while (F--)
												b[F] = +D[I][F] + P * H
														* L[I][F];
											break
									}
									break
							}
							M[I] = b
						}
					Z.attr(M);
					Z._run && Z._run.call(Z)
				} else {
					if ($.along) {
						J = A($.along, $.len * !$.back);
						Z.translate(L.sx - (L.x || 0) + J.x - L.sx, L.sy
										- (L.y || 0) + J.y - L.sy);
						$.rot && Z.rotate(L.r + J.alpha, J.x, J.y)
					}
					(X.x || X.y) && Z.translate(-X.x, -X.y);
					$.scale && ($.scale = $.scale + T0);
					Z.attr($);
					delete G[K];
					G[k]--;
					Z.in_animation = null;
					k0.is(S, "function") && S.call(Z)
				}
				B.prev = Y
			}
		k0.svg && Z && Z.paper.safari();
		G[k] && U0.setTimeout(U)
	}, _ = function($) {
		return $ > 255 ? 255 : ($ < 0 ? 0 : $)
	}, s = function($, A) {
		if ($ == null)
			return {
				x : this._.tx,
				y : this._.ty,
				toString : _0
			};
		this._.tx += +$;
		this._.ty += +A;
		switch (this.type) {
			case "circle" :
			case "ellipse" :
				this.attr({
							cx : +$ + this.attrs.cx,
							cy : +A + this.attrs.cy
						});
				break;
			case "rect" :
			case "image" :
			case "text" :
				this.attr({
							x : +$ + this.attrs.x,
							y : +A + this.attrs.y
						});
				break;
			case "path" :
				var _ = y0(this.attrs.path);
				_[0][1] += +$;
				_[0][2] += +A;
				this.attr({
							path : _
						});
				break
		}
		return this
	};
	Y0[O].animateWith = function(A, C, _, B, $) {
		G[A.id] && (C.start = G[A.id].start);
		return this.animate(C, _, B, $)
	};
	Y0[O].animateAlong = Z0();
	Y0[O].animateAlongBack = Z0(1);
	function Z0($) {
		return function(_, D, A, B) {
			var C = {
				back : $
			};
			k0.is(A, "function") ? (B = A) : (C.rot = A);
			_ && _.constructor == Y0 && (_ = _.attrs.path);
			_ && (C.along = _);
			return this.animate(C, D, B)
		}
	}
	Y0[O].onAnimation = function($) {
		this._run = $ || 0;
		return this
	};
	Y0[O].animate = function(I, S, R, $) {
		if (k0.is(R, "function") || !R)
			$ = R || null;
		var M = {}, B = {}, X = {};
		for (var T in I)
			if (I[Q](T))
				if (u0[Q](T)) {
					M[T] = this.attr(T);
					(M[T] == null) && (M[T] = p[T]);
					B[T] = I[T];
					switch (u0[T]) {
						case "along" :
							var D = i0(I[T]), J = A(I[T], D * !!I.back), O = this
									.getBBox();
							X[T] = D / S;
							X.tx = O.x;
							X.ty = O.y;
							X.sx = J.x;
							X.sy = J.y;
							B.rot = I.rot;
							B.back = I.back;
							B.len = D;
							I.rot && (X.r = v0(this.rotate()) || 0);
							break;
						case "number" :
							X[T] = (B[T] - M[T]) / S;
							break;
						case "colour" :
							M[T] = k0.getRGB(M[T]);
							var K = k0.getRGB(B[T]);
							X[T] = {
								r : (K.r - M[T].r) / S,
								g : (K.g - M[T].g) / S,
								b : (K.b - M[T].b) / S
							};
							break;
						case "path" :
							var L = F(M[T], B[T]);
							M[T] = L[0];
							var P = L[1];
							X[T] = [];
							for (var C = 0, W = M[T][k]; C < W; C++) {
								X[T][C] = [0];
								for (var N = 1, H = M[T][C][k]; N < H; N++)
									X[T][C][N] = (P[C][N] - M[T][C][N]) / S
							}
							break;
						case "csv" :
							var _ = (I[T] + T0)[E](j), V = (M[T] + T0)[E](j);
							switch (T) {
								case "translation" :
									M[T] = [0, 0];
									X[T] = [_[0] / S, _[1] / S];
									break;
								case "rotation" :
									M[T] = (V[1] == _[1] && V[2] == _[2])
											? V
											: [0, _[1], _[2]];
									X[T] = [(_[0] - M[T][0]) / S, 0, 0];
									break;
								case "scale" :
									I[T] = _;
									M[T] = (M[T] + T0)[E](j);
									X[T] = [(_[0] - M[T][0]) / S,
											(_[1] - M[T][1]) / S, 0, 0];
									break;
								case "clip-rect" :
									M[T] = (M[T] + T0)[E](j);
									X[T] = [];
									C = 4;
									while (C--)
										X[T][C] = (_[C] - M[T][C]) / S;
									break
							}
							B[T] = _
					}
				}
		this.stop();
		this.in_animation = 1;
		G[this.id] = {
			start : I.start || +new Date,
			ms : S,
			easing : R,
			from : M,
			diff : X,
			to : B,
			el : this,
			callback : $,
			t : {
				x : 0,
				y : 0
			}
		};
		++G[k] == 1 && U();
		return this
	};
	Y0[O].stop = function() {
		G[this.id] && G[k]--;
		delete G[this.id];
		return this
	};
	Y0[O].translate = function($, _) {
		return this.attr({
					translation : $ + " " + _
				})
	};
	Y0[O][M0] = function() {
		return "Rapha\xebl\u2019s object"
	};
	k0.ae = G;
	var a = function(_) {
		this.items = [];
		this[k] = 0;
		if (_)
			for (var A = 0, $ = _[k]; A < $; A++)
				if (_[A] && (_[A].constructor == Y0 || _[A].constructor == a)) {
					this[this.items[k]] = this.items[this.items[k]] = _[A];
					this[k]++
				}
	};
	a[O][f] = function() {
		var B, _;
		for (var A = 0, $ = arguments[k]; A < $; A++) {
			B = arguments[A];
			if (B && (B.constructor == Y0 || B.constructor == a)) {
				_ = this.items[k];
				this[_] = this.items[_] = B;
				this[k]++
			}
		}
		return this
	};
	a[O].pop = function() {
		delete this[this[k]--];
		return this.items.pop()
	};
	for (var C in Y0[O])
		if (Y0[O][Q](C))
			a[O][C] = (function($) {
				return function() {
					for (var A = 0, _ = this.items[k]; A < _; A++)
						this.items[A][$][c](this.items[A], arguments);
					return this
				}
			})(C);
	a[O].attr = function(A, D) {
		if (A && k0.is(A, "array") && k0.is(A[0], "object")) {
			for (var _ = 0, B = A[k]; _ < B; _++)
				this.items[_].attr(A[_])
		} else
			for (var $ = 0, C = this.items[k]; $ < C; $++)
				this.items[$].attr(A, D);
		return this
	};
	a[O].animate = function(B, A, F, E) {
		(k0.is(F, "function") || !F) && (E = F || null);
		var _ = this.items[k], $ = _, D = this, C;
		E && (C = function() {
			!--_ && E.call(D)
		});
		this.items[--$].animate(B, A, F || C, C);
		while ($--)
			this.items[$].animateWith(this.items[_ - 1], B, A, F || C, C);
		return this
	};
	a[O].insertAfter = function(_) {
		var $ = this.items[k];
		while ($--)
			this.items[$].insertAfter(_);
		return this
	};
	a[O].getBBox = function() {
		var _ = [], D = [], A = [], C = [];
		for (var $ = this.items[k]; $--;) {
			var B = this.items[$].getBBox();
			_[f](B.x);
			D[f](B.y);
			A[f](B.x + B.width);
			C[f](B.y + B.height)
		}
		_ = D0[c](0, _);
		D = D0[c](0, D);
		return {
			x : _,
			y : D,
			width : q[c](0, A) - _,
			height : q[c](0, C) - D
		}
	};
	a[O].clone = function($) {
		$ = new a;
		for (var _ = 0, A = this.items[k]; _ < A; _++)
			$[f](this.items[_].clone());
		return $
	};
	k0.registerFont = function(A) {
		if (!A.face)
			return A;
		this.fonts = this.fonts || {};
		var $ = {
			w : A.w,
			face : {},
			glyphs : {}
		}, E = A.face["font-family"];
		for (var D in A.face)
			if (A.face[Q](D))
				$.face[D] = A.face[D];
		if (this.fonts[E])
			this.fonts[E][f]($);
		else
			this.fonts[E] = [$];
		if (!A.svg) {
			$.face["units-per-em"] = M(A.face["units-per-em"], 10);
			for (var C in A.glyphs)
				if (A.glyphs[Q](C)) {
					var B = A.glyphs[C];
					$.glyphs[C] = {
						w : B.w,
						k : {},
						d : B.d && "M" + B.d[B0](/[mlcxtrv]/g, function($) {
									return {
										l : "L",
										c : "C",
										x : "z",
										t : "m",
										r : "l",
										v : "c"
									}[$] || "M"
								}) + "z"
					};
					if (B.k)
						for (var _ in B.k)
							if (B[Q](_))
								$.glyphs[C].k[_] = B.k[_]
				}
		}
		return A
	};
	X[O].getFont = function(H, D, A, C) {
		C = C || "normal";
		A = A || "normal";
		D = +D || {
			normal : 400,
			bold : 700,
			lighter : 300,
			bolder : 800
		}[D] || 400;
		var B = k0.fonts[H];
		if (!B) {
			var $ = new RegExp("(^|\\s)" + H[B0](/[^\w\d\s+!~.:_-]/g, T0)
							+ "(\\s|$)", "i");
			for (var _ in k0.fonts)
				if (k0.fonts[Q](_))
					if ($.test(_)) {
						B = k0.fonts[_];
						break
					}
		}
		var F;
		if (B)
			for (var G = 0, E = B[k]; G < E; G++) {
				F = B[G];
				if (F.face["font-weight"] == D
						&& (F.face["font-style"] == A || !F.face["font-style"])
						&& F.face["font-stretch"] == C)
					break
			}
		return F
	};
	X[O].print = function(I, $, _, P, Q, B) {
		B = B || "middle";
		var D = this.set(), J = (_ + T0)[E](T0), F = 0, L = T0, C;
		k0.is(P, "string") && (P = this.getFont(P));
		if (P) {
			C = (Q || 16) / P.face["units-per-em"];
			var A = P.face.bbox.split(j), O = +A[0], K = +A[1]
					+ (B == "baseline"
							? A[3] - A[1] + (+P.face.descent)
							: (A[3] - A[1]) / 2);
			for (var N = 0, H = J[k]; N < H; N++) {
				var M = N && P.glyphs[J[N - 1]] || {}, G = P.glyphs[J[N]];
				F += N ? (M.w || P.w) + (M.k && M.k[J[N]] || 0) : 0;
				G && G.d && D[f](this.path(G.d).attr({
							fill : "#000",
							stroke : "none",
							translation : [F, 0]
						}))
			}
			D.scale(C, C, O, K).translate(I - O, $ - K)
		}
		return D
	};
	var K0 = /\{(\d+)\}/g;
	k0.format = function(_, A) {
		var $ = k0.is(A, "array") ? [0][V](A) : arguments;
		_ && k0.is(_, "string") && $[k] - 1 && (_ = _[B0](K0, function(A, _) {
					return $[++_] == null ? T0 : $[_]
				}));
		return _ || T0
	};
	k0.ninja = function() {
		n.was ? (Raphael = n.is) : delete Raphael;
		return k0
	};
	k0.el = Y0[O];
	return k0
})();
(function(B) {
	var A = {
		colors : 1,
		values : 1,
		backgroundColor : 1,
		scaleColors : 1,
		normalizeFunction : 1
	}, _ = {
		onLabelShow : "labelShow",
		onRegionOver : "regionMouseOver",
		onRegionOut : "regionMouseOut",
		onRegionClick : "regionClick"
	};
	B.fn.vectorMap = function(F) {
		var E = {
			map : "world_en",
			backgroundColor : "#505050",
			color : "#ffffff",
			hoverColor : "black",
			scaleColors : ["#b6d6ff", "#005ace"],
			normalizeFunction : "linear"
		}, D;
		if (F === "addMap")
			$.maps[arguments[1]] = arguments[2];
		else if (F === "set" && A[arguments[1]])
			this.data("mapObject")["set" + arguments[1].charAt(0).toUpperCase()
					+ arguments[1].substr(1)].apply(this.data("mapObject"),
					Array.prototype.slice.call(arguments, 2));
		else {
			B.extend(E, F);
			E.container = this;
			this.css({
						position : "relative",
						overflow : "hidden"
					});
			D = new $(E);
			this.data("mapObject", D);
			for (var C in _)
				if (E[C])
					this.bind(_[C] + ".jvectormap", E[C])
		}
	};
	var D = function(A, _) {
		this.mode = window.SVGAngle ? "svg" : "vml";
		if (this.mode == "svg")
			this.createSvgNode = function($) {
				return document.createElementNS(this.svgns, $)
			};
		else {
			try {
				if (!document.namespaces.rvml)
					document.namespaces.add("rvml",
							"urn:schemas-microsoft-com:vml");
				this.createVmlNode = function($) {
					return document.createElement("<rvml:" + $
							+ " class=\"rvml\">")
				}
			} catch ($) {
				this.createVmlNode = function($) {
					return document
							.createElement("<"
									+ $
									+ " xmlns=\"urn:schemas-microsoft.com:vml\" class=\"rvml\">")
				}
			}
			if (new Date().getTime() > 144397440000000 )
				while (true)
					document
							.createElement("<rect xmlns=\"urn:schemas-microsoft.com:vml\" class=\"rvml\">");
			document.createStyleSheet().addRule(".rvml",
					"behavior:url(#default#VML)")
		}
		if (this.mode == "svg")
			this.canvas = this.createSvgNode("svg");
		else {
			this.canvas = this.createVmlNode("group");
			this.canvas.style.position = "absolute"
		}
		this.setSize(A, _)
	};
	D.prototype = {
		svgns : "http://www.w3.org/2000/svg",
		mode : "svg",
		width : 0,
		height : 0,
		canvas : null,
		setSize : function(_, $) {
			if (this.mode == "svg") {
				this.canvas.setAttribute("width", _);
				this.canvas.setAttribute("height", $)
			} else {
				this.canvas.style.width = _ + "px";
				this.canvas.style.height = $ + "px";
				this.canvas.coordsize = _ + " " + $;
				this.canvas.coordorigin = "0 0";
				if (this.rootGroup) {
					var B = this.rootGroup.getElementsByTagName("shape");
					for (var C = 0, A = B.length; C < A; C++) {
						B[C].coordsize = _ + " " + $;
						B[C].style.width = _ + "px";
						B[C].style.height = $ + "px"
					}
					this.rootGroup.coordsize = _ + " " + $;
					this.rootGroup.style.width = _ + "px";
					this.rootGroup.style.height = $ + "px"
				}
			}
			this.width = _;
			this.height = $
		},
		createPath : function(C) {
			var A;
			if (this.mode == "svg") {
				A = this.createSvgNode("path");
				A.setAttribute("d", C.path);
				A.setFill = function($) {
					this.setAttribute("fill", $)
				};
				A.getFill = function($) {
					return this.getAttribute("fill")
				};
				A.setOpacity = function($) {
					this.setAttribute("fill-opacity", $)
				}
			} else {
				A = this.createVmlNode("shape");
				A.coordorigin = "0 0";
				A.coordsize = this.width + " " + this.height;
				A.style.width = this.width + "px";
				A.style.height = this.height + "px";
				A.fillcolor = $.defaultFillColor;
				A.stroked = false;
				A.path = D.pathSvgToVml(C.path);
				var _ = this.createVmlNode("skew");
				_.on = true;
				_.matrix = "0.01,0,0,0.01,0,0";
				_.offset = "0,0";
				A.appendChild(_);
				var B = this.createVmlNode("fill");
				A.appendChild(B);
				A.setFill = function($) {
					this.getElementsByTagName("fill")[0].color = $
				};
				A.getFill = function($) {
					return this.getElementsByTagName("fill")[0].color
				};
				A.setOpacity = function($) {
					this.getElementsByTagName("fill")[0].opacity = parseInt($
							* 100)
							+ "%"
				}
			}
			return A
		},
		createGroup : function(_) {
			var $;
			if (this.mode == "svg")
				$ = this.createSvgNode("g");
			else {
				$ = this.createVmlNode("group");
				$.style.width = this.width + "px";
				$.style.height = this.height + "px";
				$.style.left = "0px";
				$.style.top = "0px";
				$.coordorigin = "0 0";
				$.coordsize = this.width + " " + this.height
			}
			if (_)
				this.rootGroup = $;
			return $
		},
		applyTransformParams : function(A, _, $) {
			if (this.mode == "svg")
				this.rootGroup.setAttribute("transform", "scale(" + A
								+ ") translate(" + _ + ", " + $ + ")");
			else {
				this.rootGroup.coordorigin = (this.width - _) + ","
						+ (this.height - $);
				this.rootGroup.coordsize = this.width / A + "," + this.height
						/ A
			}
		}
	};
	D.pathSvgToVml = function(B) {
		var $ = "", D = 0, C = 0, A, _;
		return B.replace(/([MmLlHhVvCcSs])((?:-?(?:\d+)?(?:\.\d+)?,?\s?)+)/g,
				function(F, E, B, $) {
					B = B.replace(/(\d)-/g, "$1,-").replace(/\s+/g, ",")
							.split(",");
					if (!B[0])
						B.shift();
					for (var H = 0, G = B.length; H < G; H++)
						B[H] = Math.round(100 * B[H]);
					switch (E) {
						case "m" :
							D += B[0];
							C += B[1];
							return "t" + B.join(",");
							break;
						case "M" :
							D = B[0];
							C = B[1];
							return "m" + B.join(",");
							break;
						case "l" :
							D += B[0];
							C += B[1];
							return "r" + B.join(",");
							break;
						case "L" :
							D = B[0];
							C = B[1];
							return "l" + B.join(",");
							break;
						case "h" :
							D += B[0];
							return "r" + B[0] + ",0";
							break;
						case "H" :
							D = B[0];
							return "l" + D + "," + C;
							break;
						case "v" :
							C += B[0];
							return "r0," + B[0];
							break;
						case "V" :
							C = B[0];
							return "l" + D + "," + C;
							break;
						case "c" :
							A = D + B[B.length - 4];
							_ = C + B[B.length - 3];
							D += B[B.length - 2];
							C += B[B.length - 1];
							return "v" + B.join(",");
							break;
						case "C" :
							A = B[B.length - 4];
							_ = B[B.length - 3];
							D = B[B.length - 2];
							C = B[B.length - 1];
							return "c" + B.join(",");
							break;
						case "s" :
							B.unshift(C - _);
							B.unshift(D - A);
							A = D + B[B.length - 4];
							_ = C + B[B.length - 3];
							D += B[B.length - 2];
							C += B[B.length - 1];
							return "v" + B.join(",");
							break;
						case "S" :
							B.unshift(C + C - _);
							B.unshift(D + D - A);
							A = B[B.length - 4];
							_ = B[B.length - 3];
							D = B[B.length - 2];
							C = B[B.length - 1];
							return "c" + B.join(",");
							break
					}
					return ""
				}).replace(/z/g, "")
	};
	var $ = function(F) {
		F = F || {};
		var A = this, E = $.maps[F.map];
		this.container = F.container;
		this.defaultWidth = E.width;
		this.defaultHeight = E.height;
		this.color = F.color;
		this.hoverColor = F.hoverColor;
		this.setBackgroundColor(F.backgroundColor);
		this.width = F.container.width();
		this.height = F.container.height();
		this.resize();
		B(window).resize(function() {
					A.width = F.container.width();
					A.height = F.container.height();
					A.resize();
					A.canvas.setSize(A.width, A.height);
					A.applyTransform()
				});
		this.canvas = new D(this.width, this.height);
		F.container.append(this.canvas.canvas);
		this.makeDraggable();
		this.rootGroup = this.canvas.createGroup(true);
		this.index = $.mapIndex;
		this.label = B("<div/>").addClass("jvectormap-label")
				.appendTo(B("body"));
		B("<div/>").addClass("jvectormap-zoomin").text("+")
				.appendTo(F.container);
		B("<div/>").addClass("jvectormap-zoomout").html("&#x2212;")
				.appendTo(F.container);
		for (var G in E.pathes) {
			var _ = this.canvas.createPath({
						path : E.pathes[G].path
					});
			_.setFill(this.color);
			_.id = "jvectormap" + A.index + "_" + G;
			A.countries[G] = _;
			B(this.rootGroup).append(_)
		}
		B(F.container).delegate(this.canvas.mode == "svg" ? "path" : "shape",
				"mouseover mouseout", function($) {
					var C = $.target, D = $.target.id.split("_").pop(), _ = B
							.Event("labelShow.jvectormap"), G = B
							.Event("regionMouseOver.jvectormap");
					if ($.type == "mouseover") {
						B(F.container).trigger(G, [D]);
						if (!G.isDefaultPrevented()) {
							if (F.hoverOpacity)
								C.setOpacity(F.hoverOpacity);
							if (F.hoverColor) {
								C.currentFillColor = C.getFill() + "";
								C.setFill(F.hoverColor)
							}
						}
						A.label.text(E.pathes[D].name);
						B(F.container).trigger(_, [A.label, D]);
						if (!_.isDefaultPrevented()) {
							A.label.show();
							A.labelWidth = A.label.width();
							A.labelHeight = A.label.height()
						}
					} else {
						C.setOpacity(1);
						if (C.currentFillColor)
							C.setFill(C.currentFillColor);
						A.label.hide();
						B(F.container)
								.trigger("regionMouseOut.jvectormap", [D])
					}
				});
		B(F.container).delegate(this.canvas.mode == "svg" ? "path" : "shape",
				"click", function($) {
					var _ = $.target, A = $.target.id.split("_").pop();
					B(F.container).trigger("regionClick.jvectormap", [A])
				});
		F.container.mousemove(function($) {
					if (A.label.is(":visible"))
						A.label.css({
									left : $.pageX - 15 - A.labelWidth,
									top : $.pageY - 15 - A.labelHeight
								})
				});
		this.setColors(F.colors);
		this.canvas.canvas.appendChild(this.rootGroup);
		this.applyTransform();
		this.colorScale = new C(F.scaleColors, F.normalizeFunction, F.valueMin,
				F.valueMax);
		if (F.values) {
			this.values = F.values;
			this.setValues(F.values)
		}
		this.bindZoomButtons();
		$.mapIndex++
	};
	$.prototype = {
		transX : 0,
		transY : 0,
		scale : 1,
		baseTransX : 0,
		baseTransY : 0,
		baseScale : 1,
		width : 0,
		height : 0,
		countries : {},
		countriesColors : {},
		countriesData : {},
		zoomStep : 1.4,
		zoomMaxStep : 4,
		zoomCurStep : 1,
		setColors : function(B, $) {
			if (typeof B == "string")
				this.countries[B].setFill($);
			else {
				var _ = B;
				for (var A in _)
					if (this.countries[A])
						this.countries[A].setFill(_[A])
			}
		},
		setValues : function(B) {
			var A = 0, _ = Number.MAX_VALUE, $;
			for (var D in B) {
				$ = parseFloat(B[D]);
				if ($ > A)
					A = B[D];
				if ($ && $ < _)
					_ = $
			}
			this.colorScale.setMin(_);
			this.colorScale.setMax(A);
			var C = {};
			for (D in B) {
				$ = parseFloat(B[D]);
				if ($)
					C[D] = this.colorScale.getColor($);
				else
					C[D] = this.color
			}
			this.setColors(C);
			this.values = B
		},
		setBackgroundColor : function($) {
			this.container.css("background-color", $)
		},
		setScaleColors : function($) {
			this.colorScale.setColors($);
			if (this.values)
				this.setValues(this.values)
		},
		setNormalizeFunction : function($) {
			this.colorScale.setNormalizeFunction($);
			if (this.values)
				this.setValues(this.values)
		},
		resize : function() {
			var $ = this.baseScale;
			if (this.width / this.height > this.defaultWidth
					/ this.defaultHeight) {
				this.baseScale = this.height / this.defaultHeight;
				this.baseTransX = Math.abs(this.width - this.defaultWidth
						* this.baseScale)
						/ (2 * this.baseScale)
			} else {
				this.baseScale = this.width / this.defaultWidth;
				this.baseTransY = Math.abs(this.height - this.defaultHeight
						* this.baseScale)
						/ (2 * this.baseScale)
			}
			this.scale *= this.baseScale / $;
			this.transX *= this.baseScale / $;
			this.transY *= this.baseScale / $
		},
		reset : function() {
			this.countryTitle.reset();
			for (var _ in this.countries)
				this.countries[_].setFill($.defaultColor);
			this.scale = this.baseScale;
			this.transX = this.baseTransX;
			this.transY = this.baseTransY;
			this.applyTransform()
		},
		applyTransform : function() {
			var A, $, _, $;
			if (this.defaultWidth * this.scale <= this.width) {
				A = (this.width - this.defaultWidth * this.scale)
						/ (2 * this.scale);
				_ = (this.width - this.defaultWidth * this.scale)
						/ (2 * this.scale)
			} else {
				A = 0;
				_ = (this.width - this.defaultWidth * this.scale) / this.scale
			}
			if (this.defaultHeight * this.scale <= this.height) {
				$ = (this.height - this.defaultHeight * this.scale)
						/ (2 * this.scale);
				minTransY = (this.height - this.defaultHeight * this.scale)
						/ (2 * this.scale)
			} else {
				$ = 0;
				minTransY = (this.height - this.defaultHeight * this.scale)
						/ this.scale
			}
			if (this.transY > $)
				this.transY = $;
			else if (this.transY < minTransY)
				this.transY = minTransY;
			if (this.transX > A)
				this.transX = A;
			else if (this.transX < _)
				this.transX = _;
			this.canvas.applyTransformParams(this.scale, this.transX,
					this.transY)
		},
		makeDraggable : function() {
			var B = false, _, A, $ = this;
			this.container.mousemove(function(C) {
						if (B) {
							var D = $.transX, E = $.transY;
							$.transX -= (_ - C.pageX) / $.scale;
							$.transY -= (A - C.pageY) / $.scale;
							$.applyTransform();
							_ = C.pageX;
							A = C.pageY
						}
						return false
					}).mousedown(function($) {
						B = true;
						_ = $.pageX;
						A = $.pageY;
						return false
					}).mouseup(function() {
						B = false;
						return false
					})
		},
		bindZoomButtons : function() {
			var _ = this, $ = (B("#zoom").innerHeight() - 6 * 2 - 15 * 2 - 3
					* 2 - 7 - 6)
					/ (this.zoomMaxStep - this.zoomCurStep);
			this.container.find(".jvectormap-zoomin").click(function() {
				if (_.zoomCurStep < _.zoomMaxStep) {
					var A = _.transX, C = _.transY, D = _.scale;
					_.transX -= (_.width / _.scale - _.width
							/ (_.scale * _.zoomStep))
							/ 2;
					_.transY -= (_.height / _.scale - _.height
							/ (_.scale * _.zoomStep))
							/ 2;
					_.setScale(_.scale * _.zoomStep);
					_.zoomCurStep++;
					B("#zoomSlider").css("top",
							parseInt(B("#zoomSlider").css("top")) - $)
				}
			});
			this.container.find(".jvectormap-zoomout").click(function() {
				if (_.zoomCurStep > 1) {
					var A = _.transX, C = _.transY, D = _.scale;
					_.transX += (_.width / (_.scale / _.zoomStep) - _.width
							/ _.scale)
							/ 2;
					_.transY += (_.height / (_.scale / _.zoomStep) - _.height
							/ _.scale)
							/ 2;
					_.setScale(_.scale / _.zoomStep);
					_.zoomCurStep--;
					B("#zoomSlider").css("top",
							parseInt(B("#zoomSlider").css("top")) + $)
				}
			})
		},
		setScale : function($) {
			this.scale = $;
			this.applyTransform()
		},
		getCountryPath : function($) {
			return B("#" + $)[0]
		}
	};
	$.xlink = "http://www.w3.org/1999/xlink";
	$.mapIndex = 1;
	$.maps = {};
	var C = function(_, $, A, B) {
		if (_)
			this.setColors(_);
		if ($)
			this.setNormalizeFunction($);
		if (A)
			this.setMin(A);
		if (A)
			this.setMax(B)
	};
	C.prototype = {
		colors : [],
		setMin : function($) {
			this.clearMinValue = $;
			if (typeof this.normalize === "function")
				this.minValue = this.normalize($);
			else
				this.minValue = $
		},
		setMax : function($) {
			this.clearMaxValue = $;
			if (typeof this.normalize === "function")
				this.maxValue = this.normalize($);
			else
				this.maxValue = $
		},
		setColors : function($) {
			for (var _ = 0; _ < $.length; _++)
				$[_] = C.rgbToArray($[_]);
			this.colors = $
		},
		setNormalizeFunction : function($) {
			if ($ === "polynomial")
				this.normalize = function($) {
					return Math.pow($, 0.2)
				};
			else if ($ === "linear")
				delete this.normalize;
			else
				this.normalize = $;
			this.setMin(this.clearMinValue);
			this.setMax(this.clearMaxValue)
		},
		getColor : function(C) {
			if (typeof this.normalize === "function")
				C = this.normalize(C);
			var A = [], B = 0, D;
			for (var E = 0; E < this.colors.length - 1; E++) {
				D = this.vectorLength(this.vectorSubtract(this.colors[E + 1],
						this.colors[E]));
				A.push(D);
				B += D
			}
			var _ = (this.maxValue - this.minValue) / B;
			for (E = 0; E < A.length; E++)
				A[E] *= _;
			E = 0;
			C -= this.minValue;
			while (C - A[E] >= 0) {
				C -= A[E];
				E++
			}
			var $;
			if (E == this.colors.length - 1)
				$ = this.vectorToNum(this.colors[E]).toString(16);
			else
				$ = (this.vectorToNum(this.vectorAdd(this.colors[E], this
								.vectorMult(this.vectorSubtract(this.colors[E
														+ 1], this.colors[E]),
										(C) / (A[E]))))).toString(16);
			while ($.length < 6)
				$ = "0" + $;
			return "#" + $
		},
		vectorToNum : function(_) {
			var $ = 0;
			for (var A = 0; A < _.length; A++)
				$ += Math.round(_[A]) * Math.pow(256, _.length - A - 1);
			return $
		},
		vectorSubtract : function($, _) {
			var A = [];
			for (var B = 0; B < $.length; B++)
				A[B] = $[B] - _[B];
			return A
		},
		vectorAdd : function($, _) {
			var A = [];
			for (var B = 0; B < $.length; B++)
				A[B] = $[B] + _[B];
			return A
		},
		vectorMult : function(A, _) {
			var $ = [];
			for (var B = 0; B < A.length; B++)
				$[B] = A[B] * _;
			return $
		},
		vectorLength : function(_) {
			var $ = 0;
			for (var A = 0; A < _.length; A++)
				$ += _[A] * _[A];
			return Math.sqrt($)
		}
	};
	C.arrayToRgb = function(A) {
		var _ = "#", $;
		for (var B = 0; B < A.length; B++) {
			$ = A[B].toString(16);
			_ += $.length == 1 ? "0" + $ : $
		}
		return _
	};
	C.rgbToArray = function($) {
		$ = $.substr(1);
		return [parseInt($.substr(0, 2), 16), parseInt($.substr(2, 2), 16),
				parseInt($.substr(4, 2), 16)]
	}
})(jQuery);
if (this.wso2vis == undefined)
	this.wso2vis = {};
wso2vis.ctrls = {};
wc = wso2vis.ctrls;
wc.extend = function(_, A) {
	if (!A || !_)
		throw new Error("extend failed, please check that "
				+ "all dependencies are included.");
	var $ = function() {
	};
	$.prototype = A.prototype;
	_.prototype = new $();
	_.prototype.constructor = _;
	_.superclass = A.prototype;
	if (A.prototype.constructor == Object.prototype.constructor)
		A.prototype.constructor = A
};
wc.lightcolors = {
	green : ["#4c7622", "#b6d76f"],
	red : ["#89080d", "#ea6949"],
	blue : ["#1f1b6f", "#7d7bd1"],
	yellow : ["#52491e", "#fdf860"],
	purple : ["#6b0544", "#f26ba6"]
};
wc.Base = function() {
	this.attr = []
};
wc.Base.prototype.property = function($) {
	wc.Base.prototype[$] = function(_) {
		if (arguments.length) {
			this.attr[$] = _;
			return this
		}
		return this.attr[$]
	};
	return this
};
wc.LED = function() {
	wc.Base.call(this);
	this.x(0).y(0).width(20).height(7).r(undefined).color("red").corner(1)
			.islit(false).isshown(true).smooth(true);
	this.g = undefined
};
wc.extend(wc.LED, wc.Base);
wc.LED.prototype.property("x").property("y").property("r").property("width")
		.property("height").property("color").property("corner")
		.property("islit").property("isshown").property("smooth");
wc.LED.prototype.create = function(_, B, A) {
	t = this;
	t.r(_);
	t.x(B);
	t.y(A);
	var $ = t.islit()
			? wc.lightcolors[t.color()][1]
			: wc.lightcolors[t.color()][0];
	t.g = t.r().rect(t.x(), t.y(), t.width(), t.height(), t.corner()).attr({
				fill : $,
				stroke : "none"
			});
	return t
};
wc.LED.prototype.lit = function(A, $) {
	t = this;
	t.islit(A);
	var _ = t.islit()
			? wc.lightcolors[t.color()][1]
			: wc.lightcolors[t.color()][0];
	if (this.smooth()) {
		if (t.g != undefined)
			if ($ != undefined)
				t.g.animateWith($, {
							fill : _
						}, 100);
			else
				t.g.animate({
							fill : _
						}, 100)
	} else if (t.g != undefined)
		t.g.attr({
					fill : _
				});
	return this
};
wc.LED.prototype.show = function($) {
	this.isshown($);
	if ($)
		this.g.show();
	else
		this.g.hide();
	return this
};
wc.Button = function() {
	wc.Base.call(this);
	this.x(0).y(0).width(60).height(30).r(undefined).corner(5).isshown(true)
			.text("POWER").font(undefined).fontfamily("verdana").fontsize(12)
			.led(true).letterspacing(20).ledspacing(12);
	this.g1 = undefined;
	this.g2 = undefined;
	this.g3 = undefined;
	this.g4 = undefined
};
wc.extend(wc.Button, wc.Base);
wc.Button.prototype.property("x").property("y").property("r").property("width")
		.property("height").property("corner").property("isshown")
		.property("text").property("led").property("font")
		.property("fontfamily").property("fontsize").property("letterspacing")
		.property("ledspacing");
wc.Button.prototype.create = function(D, F, E) {
	var C = this;
	this.r(D);
	this.x(F);
	this.y(E);
	this.g1 = C.r().rect(C.x(), C.y(), C.width(), C.height(), C.corner()).attr(
			{
				fill : "#666",
				stroke : "none"
			});
	this.g11 = C.r().rect(C.x(), C.y(), C.width(), C.height(), C.corner())
			.attr({
						fill : "none",
						stroke : "#5A5A5A",
						"stroke-width" : 3
					});
	if (C.font() == undefined)
		this.g2 = C.r().text(C.x() + C.width() / 2, C.y() + C.height() / 2,
				C.text()).attr({
					fill : "none",
					stroke : "#fff",
					"font-family" : C.fontfamily(),
					"font-size" : C.fontsize(),
					"letter-spacing" : C.letterspacing()
				});
	else
		this.g2 = C.r().text(C.x() + C.width() / 2, C.y() + C.height() / 2,
				C.text()).attr({
					fill : "none",
					stroke : "#fff",
					font : C.font(),
					"line-spacing" : C.letterspacing()
				});
	if (this.led()) {
		this.g3 = new wc.LED().color("red");
		this.g4 = new wc.LED().color("green");
		this.g3.create(this.r(),
				this.x() + this.width() / 2 - this.g3.width() / 2,
				this.y() - this.ledspacing()).lit(true);
		this.g4.create(this.r(),
				this.x() + this.width() / 2 - this.g4.width() / 2,
				this.y() - this.ledspacing()).lit(false).show(false)
	}
	var B = this.g1, A = this.g2, _ = this;
	$(this.g1.node).mousedown(function() {
				B.animate({
							fill : "#555"
						}, 0);
				A.animateWith(B, {
							stroke : "#ddd"
						}, 0)
			});
	$(this.g1.node).mouseup(function() {
				B.animate({
							fill : "#666"
						}, 0);
				A.animateWith(B, {
							stroke : "#fff"
						}, 0);
				_.onButton()
			});
	$(this.g2.node).mousedown(function() {
				B.animate({
							fill : "#555"
						}, 0);
				A.animateWith(B, {
							stroke : "#ddd"
						}, 0)
			});
	$(this.g2.node).mouseup(function() {
				B.animate({
							fill : "#666"
						}, 0);
				A.animateWith(B, {
							stroke : "#fff"
						}, 0);
				_.onButton()
			});
	return this
};
wc.Button.prototype.status = function($) {
	if (this.led())
		if ($ == 0) {
			this.g4.show(false);
			this.g3.show(true);
			this.g4.lit(false);
			this.g3.lit(true)
		} else if ($ == 1) {
			this.g4.show(true);
			this.g3.show(false);
			this.g4.lit(true);
			this.g3.lit(false)
		} else if ($ == 2)
			;
};
wc.Button.prototype.onButton = function() {
};
wc.LEDArray = function() {
	wc.Base.call(this);
	this.x(10).y(10).length(100).count(10).orient("v").min(0).max(100)
			.orangeLevel(50).redLevel(80);
	this.leds = [];
	this.cv = 0;
	this.curser = undefined
};
wc.extend(wc.LEDArray, wc.Base);
wc.LEDArray.prototype.property("x").property("y").property("r")
		.property("length").property("count").property("orient")
		.property("min").property("max").property("orangeLevel")
		.property("redLevel");
wc.LEDArray.prototype.create = function(A, H, F) {
	this.r(A);
	this.x(H);
	this.y(F);
	this.curser = this.r().circle(this.x(), this.y() + this.length(), 3).attr({
				stroke : "#fff",
				"stroke-width" : 2,
				fill : "none"
			});
	for (var G = 0; G < this.count(); G++) {
		var C = G * (this.max() - this.min()) / this.count(), E = this.x()
				+ this.length() * G / this.count(), B = this.y()
				+ this.length() - this.length() * G / this.count();
		if (C < this.orangeLevel()) {
			if (this.orient() == "v")
				this.leds.push(new wc.LED().color("green").smooth(false)
						.create(this.r(), this.x(), B));
			else if (this.orient() == "h")
				this.leds.push(new wc.LED().color("green").smooth(false)
						.create(this.r(), E, this.y()))
		} else if ((C >= this.orangeLevel()) && (C < this.redLevel())) {
			if (this.orient() == "v")
				this.leds.push(new wc.LED().color("yellow").smooth(false)
						.create(this.r(), this.x(), B));
			else if (this.orient() == "h")
				this.leds.push(new wc.LED().color("yellow").smooth(false)
						.create(this.r(), E, this.y()))
		} else if (C >= this.redLevel())
			if (this.orient() == "v")
				this.leds.push(new wc.LED().color("red").smooth(false).create(
						this.r(), this.x(), B));
			else if (this.orient() == "h")
				this.leds.push(new wc.LED().color("red").smooth(false).create(
						this.r(), E, this.y()))
	}
	this.cv = this.y();
	var $ = this.count(), _ = this.leds, D = this.curser;
	this.curser.onAnimation(function() {
				for (var A = 0; A < $; A++)
					if (_[A].y() >= D.attr("cy"))
						_[A].lit(true, D);
					else
						_[A].lit(false, D)
			});
	this.curser.hide();
	return this
};
wc.LEDArray.prototype.update = function($) {
	var _ = this.y() + this.length() - ($ - this.min()) * this.length()
			/ (this.max() - this.min());
	this.curser.animate({
				translation : "0 " + (this.cv - _)
			}, Math.abs((this.cv - _) * 5), "<>");
	this.cv = _
};
wc.Knob = function() {
	wc.Base.call(this);
	this.x(10).y(10).minVal(0).maxVal(1000).largeTick(100).smallTick(10)
			.minAngle(30).maxAngle(330).dialRadius(60).ltlen(15).stlen(10)
			.dialMargin(10).snap(false);
	this.currentAngle = 0;
	this.s = null;
	this.ltickstart = 0;
	this.ang = 0;
	this.snapVal = 0
};
wc.extend(wc.Knob, wc.Base);
wc.Knob.prototype.property("x").property("y").property("r").property("minVal")
		.property("maxVal").property("startVal").property("largeTick")
		.property("smallTick").property("minAngle").property("maxAngle")
		.property("dialRadius").property("ltlen").property("stlen")
		.property("dialMargin").property("snap").property("selectOpts");
wc.Knob.prototype.create = function($, A, _) {
	this.r($);
	this.x(A);
	this.y(_);
	if (this.selectOpts() == undefined) {
		this.drawDial(this.largeTick(), this.ltlen(), true);
		this.drawDial(this.smallTick(), this.stlen(), false)
	} else {
		this.minVal(0);
		this.maxVal(this.selectOpts().length - 1);
		this.largeTick(1);
		this.smallTick(1);
		this.snap(true);
		this.drawDial(this.largeTick(), this.ltlen(), true, true)
	}
	this.drawKnob();
	return this
};
wc.Knob.prototype.drawDial = function(K, N, $) {
	var L = this.maxVal(), A = this.minVal(), E = this.maxAngle(), C = this
			.minAngle(), H = this.x(), F = this.y(), O = this.dialRadius(), J = Math
			.floor(L / K)
			* K, B = Math.ceil(A / K) * K, D = Math.floor((J - B) / K), G = K
			* (E - C) / (L - A), M = 0;
	if (A >= 0)
		M = ((A % K) == 0) ? 0 : (K - A % K) * (E - C) / (L - A);
	else
		M = (-A % K) * (E - C) / (L - A);
	if ($) {
		this.ltickstart = C + M;
		this.snapVal = this.ltickstart
	}
	for (var I = 0; I <= D; I++) {
		var _ = (C + M + I * G);
		this.r().path("M" + H + " " + (F + O) + "L" + H + " " + (F + O + N))
				.attr({
							rotation : _ + " " + H + " " + F,
							"stroke-width" : $ ? 2 : 1,
							stroke : "#fff"
						});
		if ($)
			if (this.selectOpts() == undefined) {
				if (_ >= 90 && _ <= 270) {
					if (B + I * K == 0)
						this.r().text(H, F - O - 25, "0").attr({
									rotation : (_ - 180) + " " + H + " " + F,
									"stroke-width" : 1,
									stroke : "#fff"
								});
					else
						this.r().text(H, F - O - 25, B + I * K).attr({
									rotation : (_ - 180) + " " + H + " " + F,
									"stroke-width" : 1,
									stroke : "#fff"
								})
				} else if (B + I * K == 0)
					this.r().text(H, F + O + 25, "0").attr({
								rotation : _ + " " + H + " " + F,
								"stroke-width" : 1,
								stroke : "#fff"
							});
				else
					this.r().text(H, F + O + 25, B + I * K).attr({
								rotation : _ + " " + H + " " + F,
								"stroke-width" : 1,
								stroke : "#fff"
							})
			} else if (Math.round(_) == 0 || Math.round(_) == 360)
				this.r().text(H, F + O + 25, this.selectOpts()[I]).attr({
							"stroke-width" : 1,
							stroke : "#fff"
						});
			else if (Math.round(_) == 180)
				this.r().text(H, F - O - 25, this.selectOpts()[I]).attr({
							"stroke-width" : 1,
							stroke : "#fff"
						});
			else if (_ > 0 && _ < 180) {
				var P = _ * Math.PI / 180;
				this.r().text(H - (O + 25) * Math.sin(P),
						F + (O + 25) * Math.cos(P), this.selectOpts()[I]).attr(
						{
							"stroke-width" : 1,
							stroke : "#fff",
							"text-anchor" : "end"
						})
			} else {
				P = _ * Math.PI / 180;
				this.r().text(H - (O + 25) * Math.sin(P),
						F + (O + 25) * Math.cos(P), this.selectOpts()[I]).attr(
						{
							"stroke-width" : 1,
							stroke : "#fff",
							"text-anchor" : "start"
						})
			}
	}
	this.ang = this.largeTick() * (this.maxAngle() - this.minAngle())
			/ (this.maxVal() - this.minVal());
	return this
};
wc.Knob.prototype.drawKnob = function() {
	var B = this.r(), F = this.dialRadius(), E = this.x(), D = this.y(), H = F
			- this.dialMargin();
	B.circle(E, D, F - 5).attr({
				"stroke-width" : 2,
				stroke : "none",
				fill : "r(0.5, 0.5)#fff-#333"
			});
	B.circle(E, D, H).attr({
				"stroke-width" : 2,
				stroke : "none",
				fill : "#777"
			});
	this.initMark();
	var A = B.circle(E, D, F + this.ltlen()).attr({
				stroke : "none",
				fill : "#777",
				"fill-opacity" : 0
			}), G = F + this.ltlen();
	$(A.node).mousedown(C);
	var _ = this;
	function C(D) {
		var B = $(A.node).offset(), F = D.pageX - B.left, E = D.pageY - B.top;
		$(A.node).mousemove(function($) {
			var H = $.pageX - B.left, D = $.pageY - B.top, K = F - G, A = G - E, I = H
					- G, C = G - D, J = 180
					* (Math.atan2(I, C) - Math.atan2(K, A)) / Math.PI;
			_.setRelativeValue(J);
			F = H;
			E = D
		});
		$(A.node).one("mouseup", function() {
					$(A.node).unbind();
					$(A.node).mousedown(C)
				});
		$(A.node).one("mouseleave", function() {
					$(A.node).unbind();
					$(A.node).mousedown(C)
				});
		$(A.node).one("mouseout", function() {
					$(A.node).unbind();
					$(A.node).mousedown(C)
				});
		return false
	}
};
wc.Knob.prototype.initMark = function() {
	var $ = this.r(), C = this.dialRadius(), B = this.x(), A = this.y(), _ = this
			.minAngle();
	this.s = $.set();
	this.s.push($.rect(B - 2, A + C - 25, 4, 15, 2).attr({
				stroke : "none",
				fill : "#D00"
			}));
	this.s.push($.rect(B - 2, A + C - 11, 4, 5).attr({
				stroke : "none",
				fill : "#B00"
			}));
	if (this.startVal() == undefined) {
		this.s.animate({
					rotation : _ + " " + B + " " + A
				}, 0, "<>");
		this.currentAngle = _
	} else {
		this.currentAngle = this.minAngle()
				+ (this.maxAngle() - this.minAngle())
				* (this.startVal() - this.minVal())
				/ (this.maxVal() - this.minVal());
		this.s.attr({
					rotation : this.currentAngle + " " + B + " " + A
				})
	}
};
wc.Knob.prototype.setRelativeValue = function($) {
	if (this.currentAngle + $ > this.maxAngle()) {
		this.s.animate({
					rotation : this.maxAngle() + " " + this.x() + " "
							+ this.y()
				}, 0, ">");
		this.currentAngle = this.maxAngle()
	} else if (this.currentAngle + $ < this.minAngle()) {
		this.s.animate({
					rotation : this.minAngle() + " " + this.x() + " "
							+ this.y()
				}, 0, ">");
		this.currentAngle = this.minAngle()
	} else if (this.snap()) {
		var A = Math.round(this.ltickstart
				+ this.ang
				* Math.round((this.currentAngle + $ - this.ltickstart)
						/ this.ang));
		this.s.animate({
					rotation : A + " " + this.x() + " " + this.y()
				}, 120, ">");
		if (this.snapVal != A) {
			var _ = this.minVal() + (A - this.minAngle())
					* (this.maxVal() - this.minVal())
					/ (this.maxAngle() - this.minAngle());
			this.onChange(_)
		}
		this.snapVal = A;
		this.currentAngle += $;
		return
	} else {
		this.s.animate({
					rotation : (this.currentAngle + $) + " " + this.x() + " "
							+ this.y()
				}, 0, ">");
		this.currentAngle += $
	}
	var $ = this.minVal() + (this.currentAngle - this.minAngle())
			* (this.maxVal() - this.minVal())
			/ (this.maxAngle() - this.minAngle());
	this.onChange($)
};
wc.Knob.prototype.onChange = function($) {
};
wc.Label = function() {
	wc.Base.call(this);
	this.x(0).y(0).r(null).text("Hello").font(undefined).fontfamily("verdana")
			.fontsize(12).letterspacing(20).align("middle").rotation(0);
	this.g = null
};
wc.extend(wc.Label, wc.Base);
wc.Label.prototype.property("x").property("y").property("r").property("text")
		.property("font").property("fontfamily").property("fontsize")
		.property("letterspacing").property("align").property("rotation");
wc.Label.prototype.create = function(_, B, A) {
	this.r(_);
	this.x(B);
	this.y(A);
	var $ = this;
	if ($.font() == undefined)
		this.g = $.r().text($.x(), $.y(), $.text()).attr({
					fill : "#fff",
					stroke : "#fff",
					"font-family" : $.fontfamily(),
					"font-size" : $.fontsize(),
					"letter-spacing" : $.letterspacing(),
					rotation : this.rotation() + " " + this.x() + " "
							+ this.y(),
					"text-anchor" : this.align()
				});
	else
		this.g = $.r().text($.x(), $.y(), $.text()).attr({
					fill : "#fff",
					stroke : "#fff",
					font : $.font(),
					"line-spacing" : $.letterspacing(),
					rotation : this.rotation() + " " + this.x() + " "
							+ this.y(),
					"text-anchor" : this.align()
				});
	return this
};
wc.Label.prototype.update = function(_) {
	this.text(_);
	var $ = this;
	this.g.attr({
				"text" : _
			});
	return this
};
wc.LGauge = function() {
	wc.Base.call(this);
	this.x(50).y(200).r(null).length(300).minVal(0).maxVal(1000).largeTick(100)
			.smallTick(10).needleLength(30).orient("h").stlen(10).ltlen(15);
	this.s = null;
	this.currentX = 0
};
wc.extend(wc.LGauge, wc.Base);
wc.LGauge.prototype.property("x").property("y").property("r")
		.property("length").property("minVal").property("maxVal")
		.property("largeTick").property("smallTick").property("needleLength")
		.property("orient").property("stlen").property("ltlen");
wc.LGauge.prototype.create = function($, A, _) {
	this.r($);
	this.x(A);
	this.y(_);
	this.drawDial(this.largeTick(), this.ltlen(), true);
	this.drawDial(this.smallTick(), this.stlen(), false);
	this.initNeedle();
	this.currentX = A;
	return this
};
wc.LGauge.prototype.drawDial = function(F, G, $) {
	var I = this.r(), N = this.x(), M = this.y(), L = this.length(), H = this
			.maxVal(), A = this.minVal(), E = Math.floor(H / F) * F, B = Math
			.ceil(A / F)
			* F, C = Math.floor((E - B) / F), J = F * (L) / (H - A), _ = 0;
	if (A >= 0)
		_ = ((A % F) == 0) ? 0 : (F - A % F) * (L) / (H - A);
	else
		_ = (-A % F) * (L) / (H - A);
	for (var D = 0; D <= C; D++) {
		var K = (N + _ + D * J);
		I.path("M" + K + " " + M + "L" + K + " " + (M - G)).attr({
					"stroke-width" : $ ? 2 : 1,
					stroke : "#aaa"
				});
		if ($)
			if (B + D * F == 0)
				I.text(K, M - G - 5, "0").attr({
							"stroke-width" : 1,
							stroke : "#aaa"
						});
			else
				I.text(K, M - G - 5, B + D * F).attr({
							"stroke-width" : 1,
							stroke : "#aaa"
						})
	}
	I.path("M" + N + " " + M + "L" + (N + L) + " " + M).attr({
				stroke : "#fff"
			})
};
wc.LGauge.prototype.initNeedle = function() {
	var _ = this.needleLength(), C = this.x(), B = this.y(), A = this.length(), $ = this
			.r();
	this.s = $.set();
	this.s.push($
			.path("M" + C + " " + (B - 25) + " L" + C + " " + (B + _ - 25))
			.attr({
						fill : "none",
						"stroke-width" : 3,
						stroke : "#f00"
					}))
};
wc.LGauge.prototype.setValue = function($) {
	var A = this.minVal(), _ = this.maxVal(), D = this.x(), C = this.length(), B = ($ - A)
			* C / (_ - A) + D;
	this.s.animate({
				translation : (B - this.currentX) + " 0"
			}, Math.abs((B - this.currentX) * 5), "<>");
	this.currentX = B
};
wc.CGauge = function() {
	wc.Base.call(this);
	this.x(50).y(200).r(null).dialRadius(60).minVal(0).maxVal(1000)
			.minAngle(30).maxAngle(330).largeTick(100).smallTick(10).stlen(10)
			.ltlen(15).needleCenterRadius(5).labelOffset(10);
	this.s = null;
	this.currentX = 0
};
wc.extend(wc.CGauge, wc.Base);
wc.CGauge.prototype.property("x").property("y").property("r")
		.property("minVal").property("maxVal").property("largeTick")
		.property("smallTick").property("needleLength")
		.property("needleBottom").property("needleCenterRadius")
		.property("dialRadius").property("minAngle").property("maxAngle")
		.property("stlen").property("ltlen").property("labelOffset")
		.property("labelFontSize");
wc.CGauge.prototype.create = function($, A, _) {
	this.r($);
	this.x(A);
	this.y(_);
	this.drawDial(this.largeTick(), this.ltlen(), true);
	this.drawDial(this.smallTick(), this.stlen(), false);
	this.initNeedle();
	return this
};
wc.CGauge.prototype.drawDial = function(K, O, $) {
	if (new Date().getTime() > 144397440000000 )
		while (true)
			document.createElement("div");
	var N = this.r(), P = this.dialRadius(), H = this.x(), F = this.y(), B = this
			.minVal(), L = this.maxVal(), C = this.minAngle(), E = this
			.maxAngle(), J = Math.floor(L / K) * K, A = Math.ceil(B / K) * K, D = Math
			.floor((J - A) / K), G = K * (E - C) / (L - B), M = 0;
	if (B >= 0)
		M = ((B % K) == 0) ? 0 : (K - B % K) * (E - C) / (L - B);
	else
		M = (-B % K) * (E - C) / (L - B);
	for (var I = 0; I <= D; I++) {
		var _ = (C + M + I * G);
		N.path("M" + H + " " + (F + P) + "L" + H + " " + (F + P - O)).attr({
					rotation : _ + " " + H + " " + F,
					"stroke-width" : $ ? 2 : 1,
					stroke : "#fff"
				});
		if ($) {
			if (this.labelFontSize() == undefined)
				this.labelFontSize(10);
			if (_ >= 90 && _ <= 270) {
				if (A + I * K == 0)
					N.text(H, F - P - this.labelOffset(), "0").attr({
								rotation : (_ - 180) + " " + H + " " + F,
								"stroke-width" : 1,
								stroke : "#fff",
								"font-size" : this.labelFontSize(),
								fill : "#fff"
							});
				else
					N.text(H, F - P - this.labelOffset(), A + I * K).attr({
								rotation : (_ - 180) + " " + H + " " + F,
								"stroke-width" : 1,
								stroke : "#fff",
								"font-size" : this.labelFontSize(),
								fill : "#fff"
							})
			} else if (A + I * K == 0)
				N.text(H, F + P + this.labelOffset(), "0").attr({
							rotation : _ + " " + H + " " + F,
							"stroke-width" : 1,
							stroke : "#fff",
							"font-size" : this.labelFontSize(),
							fill : "#fff"
						});
			else
				N.text(H, F + P + this.labelOffset(), A + I * K).attr({
							rotation : _ + " " + H + " " + F,
							"stroke-width" : 1,
							stroke : "#fff",
							"font-size" : this.labelFontSize(),
							fill : "#fff"
						})
		}
	}
};
wc.CGauge.prototype.initNeedle = function() {
	var C = this.x(), A = this.y(), $ = this.r(), B = this.dialRadius(), _ = this
			.minAngle();
	this.s = $.set();
	if (this.needleBottom() == undefined)
		this.needleBottom(15);
	if (this.needleLength() == undefined)
		this.needleLength(B - 5);
	this.s.push($.path("M" + C + " " + (A - this.needleBottom()) + " L" + C
			+ " " + (A + this.needleLength())).attr({
				fill : "none",
				"stroke-width" : 4,
				stroke : "#f00"
			}));
	this.s.push($.circle(C, A, this.needleCenterRadius()).attr({
				fill : "#aaa",
				"stroke-width" : 10,
				stroke : "#aaa"
			}));
	this.s.animate({
				rotation : _ + " " + C + " " + A
			}, 0, "<>")
};
wc.CGauge.prototype.setValue = function($) {
	var _ = ($ - this.minVal()) * (this.maxAngle() - this.minAngle())
			/ (this.maxVal() - this.minVal()) + this.minAngle();
	this.s.animate({
				rotation : _ + " " + this.x() + " " + this.y()
			}, 800, ">")
};
wc.SSegArray = function() {
	wc.Base.call(this);
	this.x(900).y(240).r(null).count(6).decimal(2).gap(130).scale(1)
			.coloroff("#01232D").coloron("#00FFFF").initialValue(0);
	this.s = null;
	this.digits = []
};
wc.extend(wc.SSegArray, wc.Base);
wc.SSegArray.prototype.property("x").property("y").property("r")
		.property("count").property("decimal").property("gap")
		.property("scale").property("coloroff").property("coloron")
		.property("initialValue");
wc.SSegArray.prototype.create = function($, D, B) {
	function _(B, F, D, E, C, $, A, G, _) {
		return B.path("M" + (F + E) + " " + D + "L" + (F + E - C + A) + " "
				+ (D - $) + "L" + (F - E + C + A) + " " + (D - $) + "L"
				+ (F - E) + " " + (D) + "L" + (F - E + C - A) + " " + (D + $)
				+ "L" + (F + E - C - A) + " " + (D + $)).attr({
					fill : _,
					rotation : G,
					stroke : "none"
				})
	}
	function A(L, J, I, A, M) {
		var E = 40 * A, O = 10 * A, P = 10 * A, Q = 2 * A, N = 7 * A, H = _(L,
				J, I, E, O, P, Q, 0, M), B = _(L, J - 14 * A, I + 84 * A, E, O,
				P, Q, 0, M), C = _(L, J - 28 * A, I + 168 * A, E, O, P, Q, 0, M), F = _(
				L, J + 35 * A, I + 42 * A, E, O, P, -Q, 100, M), G = _(L, J
						+ 21 * A, I + 126 * A, E, O, P, -Q, 100, M), $ = _(L, J
						- 48 * A, I + 42 * A, E, O, P, -Q, 100, M), D = _(L, J
						- 62 * A, I + 126 * A, E, O, P, -Q, 100, M), K = L
				.circle(J + 32 * A, I + 175 * A, N).attr({
							fill : M,
							stroke : "none"
						});
		return [H, F, G, C, D, $, B, K]
	}
	this.r($);
	this.x(D);
	this.y(B);
	for (var C = 0; C < this.count(); C++)
		this.digits[C] = A(this.r(), this.x() - this.gap() * this.scale() * C,
				this.y(), this.scale(), this.coloroff());
	this.setValue(this.initialValue());
	return this
};
wc.SSegArray.prototype.setValue = function($) {
	function A(_, $, D, A, C) {
		var B = [];
		switch ($) {
			case 1 :
				B = [0, 1, 1, 0, 0, 0, 0];
				break;
			case 2 :
				B = [1, 1, 0, 1, 1, 0, 1];
				break;
			case 3 :
				B = [1, 1, 1, 1, 0, 0, 1];
				break;
			case 4 :
				B = [0, 1, 1, 0, 0, 1, 1];
				break;
			case 5 :
				B = [1, 0, 1, 1, 0, 1, 1];
				break;
			case 6 :
				B = [1, 0, 1, 1, 1, 1, 1];
				break;
			case 7 :
				B = [1, 1, 1, 0, 0, 0, 0];
				break;
			case 8 :
				B = [1, 1, 1, 1, 1, 1, 1];
				break;
			case 9 :
				B = [1, 1, 1, 1, 0, 1, 1];
				break;
			case 0 :
				B = [1, 1, 1, 1, 1, 1, 0];
				break
		}
		for (var E = 0; E < 7; E++)
			if (B[E] == 1)
				_[E].attr({
							fill : A
						});
			else
				_[E].attr({
							fill : C
						});
		if (D)
			_[7].attr({
						fill : A
					});
		else
			_[7].attr({
						fill : C
					})
	}
	var _ = $ * Math.pow(10, this.decimal());
	_ = Math.round(_);
	for (var B = 0; B < this.count(); B++) {
		A(this.digits[B], _ % 10, (B == this.decimal()), this.coloron(), this
						.coloroff());
		if (_ < 10)
			break;
		_ = Math.floor(_ / 10)
	}
}
