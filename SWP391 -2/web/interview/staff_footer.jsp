<%@page contentType="text/html" pageEncoding="UTF-8"%>
            </main>
        </div>
        <script>
            // Client-side pagination: add data-page-size="N" to a <table> to enable.
            // Hidden rows stay in the form, so checkboxes/radios are still submitted.
            document.addEventListener("DOMContentLoaded", function () {
                document.querySelectorAll("table[data-page-size]").forEach(function (table) {
                    var size = parseInt(table.getAttribute("data-page-size"), 10) || 5;
                    var allRows = Array.prototype.slice.call(table.tBodies[0].rows);
                    // Group heading rows (class "iv-date-heading") are not counted as data rows.
                    var rows = [];
                    var currentHeading = null;
                    allRows.forEach(function (r) {
                        if (r.classList.contains("iv-date-heading")) { currentHeading = r; return; }
                        r._heading = currentHeading;
                        rows.push(r);
                    });
                    var pages = Math.ceil(rows.length / size);
                    if (pages <= 1) return;

                    var nav = document.createElement("div");
                    nav.style.cssText = "display:flex; gap:6px; align-items:center; justify-content:flex-end; flex-wrap:wrap; margin:10px 0 16px;";
                    table.parentNode.insertBefore(nav, table.nextSibling);

                    function btn(label, page, disabled, active) {
                        var b = document.createElement("button");
                        b.type = "button";   // do not submit the form
                        b.textContent = label;
                        b.className = "iv-btn iv-btn-sm" + (active ? "" : " iv-btn-ghost");
                        b.style.margin = "0";
                        b.disabled = disabled;
                        b.addEventListener("click", function () { show(page); });
                        return b;
                    }

                    function show(page) {
                        table.querySelectorAll("tr.pg-clone").forEach(function (c) { c.remove(); });
                        allRows.forEach(function (r) {
                            if (r.classList.contains("iv-date-heading")) r.style.display = "none";
                        });
                        var prevHeading = null;
                        rows.forEach(function (r, i) {
                            var visible = i >= (page - 1) * size && i < page * size;
                            r.style.display = visible ? "" : "none";
                            if (!visible) return;
                            var h = r._heading;
                            if (h && h !== prevHeading) {
                                if (h.nextElementSibling === r) {
                                    h.style.display = "";
                                } else {
                                    // The date group started on an earlier page: repeat its heading.
                                    var clone = h.cloneNode(true);
                                    clone.classList.add("pg-clone");
                                    clone.style.display = "";
                                    r.parentNode.insertBefore(clone, r);
                                }
                            }
                            prevHeading = h;
                        });
                        nav.innerHTML = "";
                        nav.appendChild(btn("\u2039 Tr\u01B0\u1EDBc", page - 1, page === 1, false));
                        for (var p = 1; p <= pages; p++) nav.appendChild(btn(String(p), p, false, p === page));
                        nav.appendChild(btn("Sau \u203A", page + 1, page === pages, false));
                    }
                    show(1);
                });
            });
        </script>
    </body>
</html>
