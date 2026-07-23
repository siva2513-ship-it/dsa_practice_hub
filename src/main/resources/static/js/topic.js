// DSA Practice Hub - Shared Frontend Client Application Logic

document.addEventListener("DOMContentLoaded", () => {
    initApp();
});

function initApp() {
    const path = window.location.pathname;
    const pageName = path.substring(path.lastIndexOf('/') + 1);

    // Skip homepage
    if (pageName === "" || pageName === "index.html") {
        return;
    }

    // Determine current topic ID from filename
    let topicId = "";
    if (pageName !== "problems.html") {
        topicId = pageName.replace(".html", "");
    }

    // Find main container to inject App Structure
    // All specific topic files have <section class="section-container" id="topics">
    const container = document.getElementById("topics");
    if (!container) {
        console.error("DOM container #topics not found");
        return;
    }

    // Check if we need to render the topic selector (only on global problems page)
    const showTopicFilter = (pageName === "problems.html");

    // Inject Filter Controls and Problems Table Container
    container.innerHTML += `
        <div class="filter-controls">
            <div class="filter-group">
                <label for="search-input">Search Name</label>
                <input type="text" id="search-input" class="filter-input" placeholder="Search problems...">
            </div>
            
            <div class="filter-group" id="topic-filter-group" style="${showTopicFilter ? '' : 'display: none;'}">
                <label for="topic-select">DSA Topic</label>
                <select id="topic-select" class="filter-select">
                    <option value="">All Topics</option>
                    <option value="arrays">Arrays</option>
                    <option value="strings">Strings</option>
                    <option value="sorting">Sorting</option>
                    <option value="searching">Searching</option>
                    <option value="binary-search">Binary Search</option>
                    <option value="two-pointers">Two Pointers</option>
                    <option value="prefix-sum">Prefix Sum</option>
                    <option value="hashing">Hashing</option>
                    <option value="stack">Stack</option>
                    <option value="queue">Queue</option>
                    <option value="linked-list">Linked List</option>
                    <option value="recursion">Recursion</option>
                    <option value="backtracking">Backtracking</option>
                    <option value="greedy">Greedy</option>
                    <option value="dynamic-programming">Dynamic Programming</option>
                    <option value="trees">Trees</option>
                    <option value="graphs">Graphs</option>
                    <option value="bfs">BFS</option>
                    <option value="dfs">DFS</option>
                    <option value="shortest-paths">Shortest Paths</option>
                    <option value="disjoint-set-union">Disjoint Set Union</option>
                    <option value="bit-manipulation">Bit Manipulation</option>
                    <option value="number-theory">Number Theory</option>
                    <option value="combinatorics">Combinatorics</option>
                </select>
            </div>

            <div class="filter-group">
                <label for="difficulty-select">Difficulty</label>
                <select id="difficulty-select" class="filter-select">
                    <option value="">All Difficulties</option>
                    <option value="easy">Easy (≤ 1200)</option>
                    <option value="medium">Medium (1200 - 1700)</option>
                    <option value="hard">Hard (> 1700)</option>
                </select>
            </div>

            <div class="filter-group">
                <label for="sort-select">Sort Rating</label>
                <select id="sort-select" class="filter-select">
                    <option value="">No Sorting</option>
                    <option value="low-high">Rating: Low → High</option>
                    <option value="high-low">Rating: High → Low</option>
                </select>
            </div>
        </div>

        <div id="problems-content">
            <div class="problems-table-container">
                <table class="problems-table">
                    <thead>
                        <tr>
                            <th style="width: 140px;">Status</th>
                            <th>Problem Name</th>
                            <th style="width: 100px; text-align: center;">Rating</th>
                            <th style="width: 120px; text-align: center;">Difficulty</th>
                            <th>Tags</th>
                            <th style="width: 100px; text-align: center;">Link</th>
                        </tr>
                    </thead>
                    <tbody id="problems-tbody">
                        <!-- Problems loaded dynamically -->
                    </tbody>
                </table>
            </div>
            <div id="loading-indicator" class="loading-container" style="display: none;">
                <div class="loading-spinner"></div>
                <div class="loading-text">Loading practice problems from Codeforces...</div>
            </div>
            <div id="error-message" class="error-message-box" style="display: none;"></div>
            <div id="empty-state" class="problems-view-box" style="display: none;">
                <div class="empty-state-card">
                    <div class="empty-illustration">
                        <svg viewBox="0 0 24 24" width="48" height="48" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" class="illustration-svg">
                            <circle cx="12" cy="12" r="10"></circle>
                            <line x1="8" y1="12" x2="16" y2="12"></line>
                        </svg>
                    </div>
                    <h3 class="empty-title">No Problems Found</h3>
                    <p class="empty-description">Try adjusting your filters or search terms to find practice problems.</p>
                </div>
            </div>
        </div>
    `;

    // Local states
    let localProblems = [];

    // Bind DOM items
    const searchInput = document.getElementById("search-input");
    const topicSelect = document.getElementById("topic-select");
    const difficultySelect = document.getElementById("difficulty-select");
    const sortSelect = document.getElementById("sort-select");

    // Initialize inputs if topic pre-selected
    if (topicId && topicSelect) {
        topicSelect.value = topicId;
    }

    // Load problems function
    function fetchAndRenderProblems() {
        const queryParams = new URLSearchParams();

        // Topic selection
        const currTopic = showTopicFilter ? (topicSelect ? topicSelect.value : "") : topicId;
        if (currTopic) {
            queryParams.append("tag", currTopic);
        }

        // Search filter
        if (searchInput && searchInput.value.trim()) {
            queryParams.append("search", searchInput.value.trim());
        }

        // Difficulty filter
        if (difficultySelect && difficultySelect.value) {
            queryParams.append("difficulty", difficultySelect.value);
        }

        // Limit results payload
        queryParams.append("limit", "150");

        // UI state toggle
        showLoading(true);
        hideError();
        hideEmpty();
        clearTable();

        fetch(`/api/problems?${queryParams.toString()}`)
            .then(res => {
                if (!res.ok) {
                    throw new Error(`HTTP error! status: ${res.status}`);
                }
                return res.json();
            })
            .then(data => {
                localProblems = data;
                renderData(localProblems);
            })
            .catch(err => {
                console.error("API call failed:", err);
                showError("The practice service backend is currently unavailable. Ensure the Spring Boot backend server is running.");
            })
            .finally(() => {
                showLoading(false);
            });
    }

    // Render logic
    function renderData(problems) {
        clearTable();

        // Check sorting selection
        const sortBy = sortSelect ? sortSelect.value : "";
        if (sortBy === "low-high") {
            problems.sort((a, b) => (a.rating || 0) - (b.rating || 0));
        } else if (sortBy === "high-low") {
            problems.sort((a, b) => (b.rating || 0) - (a.rating || 0));
        }

        if (!problems || problems.length === 0) {
            showEmpty();
            return;
        }

        hideEmpty();
        const tbody = document.getElementById("problems-tbody");
        const statusMap = getLocalStatusMap();

        problems.forEach(p => {
            const key = `${p.contestId}_${p.index}`;
            const state = statusMap[key] || "unsolved";

            const tr = document.createElement("tr");

            // Status dropdown cell
            const statusCell = document.createElement("td");
            const select = document.createElement("select");
            select.className = `status-select ${state}`;
            select.innerHTML = `
                <option value="unsolved" ${state === 'unsolved' ? 'selected' : ''}>Unsolved</option>
                <option value="attempted" ${state === 'attempted' ? 'selected' : ''}>Attempted</option>
                <option value="solved" ${state === 'solved' ? 'selected' : ''}>Solved</option>
            `;
            select.addEventListener("change", (e) => {
                const newState = e.target.value;
                select.className = `status-select ${newState}`;
                updateLocalStatus(key, newState);
            });
            statusCell.appendChild(select);
            tr.appendChild(statusCell);

            // Problem Name Cell
            const pLink = `https://codeforces.com/problemset/problem/${p.contestId}/${p.index}`;
            const nameCell = document.createElement("td");
            nameCell.innerHTML = `<a href="${pLink}" target="_blank" class="problem-link">${p.contestId}${p.index} - ${p.name}</a>`;
            tr.appendChild(nameCell);

            // Rating Cell
            const ratingCell = document.createElement("td");
            ratingCell.style.textAlign = "center";
            ratingCell.style.fontWeight = "700";
            ratingCell.style.fontFamily = "var(--font-mono)";
            ratingCell.textContent = p.rating ? p.rating : "—";
            tr.appendChild(ratingCell);

            // Difficulty Cell
            const diffCell = document.createElement("td");
            diffCell.style.textAlign = "center";
            let diffClass = "easy";
            let diffLabel = "Easy";
            if (p.rating) {
                if (p.rating > 1700) {
                    diffClass = "hard";
                    diffLabel = "Hard";
                } else if (p.rating > 1200) {
                    diffClass = "medium";
                    diffLabel = "Medium";
                }
            } else {
                diffLabel = "—";
            }
            diffCell.innerHTML = diffLabel !== "—" ? `<span class="difficulty-indicator ${diffClass}">${diffLabel}</span>` : "—";
            tr.appendChild(diffCell);

            // Tags Cell
            const tagsCell = document.createElement("td");
            if (p.tags && p.tags.length > 0) {
                p.tags.forEach(t => {
                    const span = document.createElement("span");
                    span.className = "tag-pill";
                    span.textContent = t;
                    tagsCell.appendChild(span);
                });
            } else {
                tagsCell.textContent = "—";
            }
            tr.appendChild(tagsCell);

            // Action / Link Cell
            const linkCell = document.createElement("td");
            linkCell.style.textAlign = "center";
            linkCell.innerHTML = `
                <a href="${pLink}" target="_blank" class="solve-btn">
                    <span>Solve</span>
                    <svg viewBox="0 0 24 24" width="12" height="12" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M18 13v6a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h6"></path>
                        <polyline points="15 3 21 3 21 9"></polyline>
                        <line x1="10" y1="14" x2="21" y2="3"></line>
                    </svg>
                </a>
            `;
            tr.appendChild(linkCell);

            tbody.appendChild(tr);
        });
    }

    // Helper functions
    function clearTable() {
        const tbody = document.getElementById("problems-tbody");
        if (tbody) tbody.innerHTML = "";
    }

    function showLoading(show) {
        const loader = document.getElementById("loading-indicator");
        if (loader) loader.style.display = show ? "flex" : "none";
    }

    function showError(msg) {
        const errorBox = document.getElementById("error-message");
        if (errorBox) {
            errorBox.textContent = msg;
            errorBox.style.display = "block";
        }
    }

    function hideError() {
        const errorBox = document.getElementById("error-message");
        if (errorBox) errorBox.style.display = "none";
    }

    function showEmpty() {
        const empty = document.getElementById("empty-state");
        if (empty) empty.style.display = "flex";
    }

    function hideEmpty() {
        const empty = document.getElementById("empty-state");
        if (empty) empty.style.display = "none";
    }

    // Event listener bindings with debounce for search
    let debounceTimer;
    if (searchInput) {
        searchInput.addEventListener("input", () => {
            clearTimeout(debounceTimer);
            debounceTimer = setTimeout(() => {
                fetchAndRenderProblems();
            }, 300);
        });
    }

    if (topicSelect) {
        topicSelect.addEventListener("change", () => {
            fetchAndRenderProblems();
        });
    }

    if (difficultySelect) {
        difficultySelect.addEventListener("change", () => {
            fetchAndRenderProblems();
        });
    }

    if (sortSelect) {
        sortSelect.addEventListener("change", () => {
            renderData(localProblems);
        });
    }

    // Initial Trigger
    fetchAndRenderProblems();
}

// Local Storage Solving State management helper functions
function getLocalStatusMap() {
    try {
        const data = localStorage.getItem("dsa_practice_status");
        return data ? JSON.parse(data) : {};
    } catch (e) {
        console.error("Local storage error:", e);
        return {};
    }
}

function updateLocalStatus(problemKey, status) {
    try {
        const map = getLocalStatusMap();
        map[problemKey] = status;
        localStorage.setItem("dsa_practice_status", JSON.stringify(map));
    } catch (e) {
        console.error("Local storage write error:", e);
    }
}