package com.example.kvizko.web;

import com.example.kvizko.exceptions.InvalidCredentialsException;
import com.example.kvizko.exceptions.UsernameAlreadyTakenException;
import com.example.kvizko.models.*;

import com.example.kvizko.models.views.*;
import com.example.kvizko.repository.*;
import com.example.kvizko.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@org.springframework.stereotype.Controller
public class Controller {

    private final QuizService quizService;
    private final CategoryService categoryService;
    private final SubjectService subjectService;
    private final QuestionService questionService;
    private final ChoiceService choiceService;
    private final UserService userService;
    private final AttemptService attemptService;
    private final ResultService resultService;
    private final QuizTakerService quizTakerService;
    private final SelectedchoiceService selectedchoiceService;

    private final AdministratorRepository administratorRepository;

    private final AvgpoenizakvizRepository avgpoenizakvizRepository;
    private final BrturniribrigrachibrkvizovibrmedaliRepository brturniribrigrachibrkvizovibrmedaliRepository;
    private final IzveshtajzaturnirRepository izveshtajzaturnirRepository;
    private final KorisnicirangiranisporedmedaliRepository korisnicirangiranisporedmedaliRepository;
    private final KvizovirangiranisporedtochniprashanjaRepository kvizovirangiranisporedtochniprashanjaRepository;
    private final NajangazhiranikorisniciRepository najangazhiranikorisniciRepository;
    private final NajigranikvizoviRepository najigranikvizoviRepository;
    private final PrashanjarangiranisporedtochniodgovoriRepository prashanjarangiranisporedtochniodgovoriRepository;
    private final Top5ReshavachiRepository top5ReshavachiRepository;
    private final Vkbrojreshenikvizovivo3MeseciRepository vkbrojreshenikvizovivo3MeseciRepositoryRepository;
    private final LeaderboardRepository leaderboardRepository;


    public Controller(QuizService quizService, CategoryService categoryService, SubjectService subjectService,
                      QuestionService questionService, ChoiceService choiceService, UserService userService,
                      AttemptService attemptService, ResultService resultService, QuizTakerService quizTakerService,
                      SelectedchoiceService selectedchoiceService, AdministratorRepository administratorRepository, AvgpoenizakvizRepository avgpoenizakvizRepository,
                      BrturniribrigrachibrkvizovibrmedaliRepository brturniribrigrachibrkvizovibrmedaliRepository,
                      IzveshtajzaturnirRepository izveshtajzaturnirRepository,
                      KorisnicirangiranisporedmedaliRepository korisnicirangiranisporedmedaliRepository,
                      KvizovirangiranisporedtochniprashanjaRepository kvizovirangiranisporedtochniprashanjaRepository,
                      NajangazhiranikorisniciRepository najangazhiranikorisniciRepository,
                      NajigranikvizoviRepository najigranikvizoviRepository,
                      PrashanjarangiranisporedtochniodgovoriRepository prashanjarangiranisporedtochniodgovoriRepository,
                      Top5ReshavachiRepository top5ReshavachiRepository,
                      Vkbrojreshenikvizovivo3MeseciRepository vkbrojreshenikvizovivo3MeseciRepositoryRepository, LeaderboardRepository leaderboardRepository) {
        this.quizService = quizService;
        this.categoryService = categoryService;
        this.subjectService = subjectService;
        this.questionService = questionService;
        this.choiceService = choiceService;
        this.userService = userService;
        this.attemptService = attemptService;
        this.resultService = resultService;
        this.quizTakerService = quizTakerService;
        this.selectedchoiceService = selectedchoiceService;
        this.administratorRepository = administratorRepository;
        this.avgpoenizakvizRepository = avgpoenizakvizRepository;
        this.brturniribrigrachibrkvizovibrmedaliRepository = brturniribrigrachibrkvizovibrmedaliRepository;
        this.izveshtajzaturnirRepository = izveshtajzaturnirRepository;
        this.korisnicirangiranisporedmedaliRepository = korisnicirangiranisporedmedaliRepository;
        this.kvizovirangiranisporedtochniprashanjaRepository = kvizovirangiranisporedtochniprashanjaRepository;
        this.najangazhiranikorisniciRepository = najangazhiranikorisniciRepository;
        this.najigranikvizoviRepository = najigranikvizoviRepository;
        this.prashanjarangiranisporedtochniodgovoriRepository = prashanjarangiranisporedtochniodgovoriRepository;
        this.top5ReshavachiRepository = top5ReshavachiRepository;
        this.vkbrojreshenikvizovivo3MeseciRepositoryRepository = vkbrojreshenikvizovivo3MeseciRepositoryRepository;
        this.leaderboardRepository = leaderboardRepository;
    }


    public boolean privilegeCheck(Long userid) {
        return administratorRepository.findById(userid).isPresent();
    }

    private void setPrivilege(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null && privilegeCheck(user.getUserid())) {
            model.addAttribute("isAdmin", true);
        } else {
            model.addAttribute("isAdmin", false);
        }
    }

    private boolean redirectNonAdmin(HttpSession session) {
        User user = (User) session.getAttribute("user");
        return user == null || !privilegeCheck(user.getUserid());
    }


    @GetMapping("/")
    public String index(Model model, HttpSession session) {
        model.addAttribute("subjects", subjectService.listAll());
        model.addAttribute("user", session.getAttribute("user"));
        setPrivilege(model, session);
        return "index";
    }

    @GetMapping("/{subjectid}/categories")
    public String selectCategory(@PathVariable Long subjectid, Model model, HttpSession session) {
        model.addAttribute("categories", categoryService.findBySubject(subjectid));
        model.addAttribute("user", session.getAttribute("user"));
        setPrivilege(model, session);

        return "Quizzes-and-categories";
    }

    @GetMapping("/{categoryid}/quizzes")
    public String selectQuiz(@PathVariable Long categoryid, Model model, HttpSession session) {
        model.addAttribute("quizzes", quizService.quizzesByCategoryID(categoryid));
        model.addAttribute("user", session.getAttribute("user"));
        setPrivilege(model, session);

        return "Quizzes-and-categories";
    }

    @GetMapping("/{quizid}/quizStart")
    public String quizStart(@PathVariable Long quizid, Model model, HttpSession session) {

        Attempt attempt = null;
        if (session.getAttribute("user") != null) {
            User user = (User) session.getAttribute("user");
            Quiztaker quiztaker = quizTakerService.findById(user.getUserid());
            if (quiztaker != null) {
                attempt = attemptService.save(quiztaker, java.sql.Date.valueOf(LocalDate.now()), quizService.quizById(quizid));
            }


        }

        session.setAttribute("attempt", attempt);
        model.addAttribute("user", session.getAttribute("user"));
        setPrivilege(model, session);

        List<Question> questionsByQuiz = questionService.questionsByQuiz(quizid);
        Collections.shuffle(questionsByQuiz);

        questionsByQuiz=questionsByQuiz.stream().limit(5).collect(Collectors.toList());

        List<Choice> correctChoices = new ArrayList<>();
        List<Question> allQuestions = new ArrayList<>(questionsByQuiz);
        for(Question question : allQuestions)
        {
            correctChoices.addAll( this.choiceService.choicesByQuestion(question).stream().filter(Choice::isIscorrect).toList());
        }
        session.setAttribute("correctChoices", correctChoices);
        session.setAttribute("allQuestions",allQuestions);

        Question firstQuestion = questionsByQuiz.remove(0);
        model.addAttribute("question", firstQuestion);

        session.setAttribute("questionsByQuiz", questionsByQuiz);

        session.setAttribute("quizName", quizService.quizById(quizid).getQuizname());
        session.setAttribute("questionCount", 5);
        session.setAttribute("correctQuestionCounter", 0);


        List<Choice> choicesByQuestion = choiceService.choicesByQuestion(firstQuestion);
        Collections.shuffle(choicesByQuestion);
        model.addAttribute("choices", choicesByQuestion);
        model.addAttribute("lastQuestion", false);

        session.setAttribute("selectedChoices", new ArrayList<Choice>());

        return "Question-and-choices";
    }


    @PostMapping("/quizSolving")
    public String quizSolving(Model model,
                              @RequestParam(required = false) Long selectedChoice,
                              @SessionAttribute Integer questionCount,
                              @SessionAttribute List<Question> questionsByQuiz,
                              @SessionAttribute String quizName,
                              @SessionAttribute Integer correctQuestionCounter,
                              HttpSession session) {

        model.addAttribute("user", session.getAttribute("user"));
        setPrivilege(model, session);

        List<Choice> selectedChoices=(List<Choice>) session.getAttribute("selectedChoices");
        Choice selectedChoiceObj=choiceService.getById(selectedChoice);
        selectedChoices.add(selectedChoiceObj);
        session.setAttribute("selectedChoices", selectedChoices);

        if (questionsByQuiz.isEmpty()) {


            if (selectedChoice != null && choiceService.getById(selectedChoice).isIscorrect()) {
                correctQuestionCounter++;
            }

            Attempt attempt = (Attempt) session.getAttribute("attempt");
            if (attempt != null) {
                if (selectedChoice != null) {
                    selectedchoiceService.save(selectedChoice, attempt);

                }
                resultService.save(attempt, correctQuestionCounter * 100 / questionCount);
            }

            model.addAttribute("result", correctQuestionCounter * 100 / questionCount);
            model.addAttribute("allQuestions", session.getAttribute("allQuestions"));
            model.addAttribute("correctChoices", session.getAttribute("correctChoices"));
            model.addAttribute("selectedChoices", selectedChoices);
            return "Result";
        } else {
            if (questionsByQuiz.size() == 1) {
                model.addAttribute("lastQuestion", true);
            } else {
                model.addAttribute("lastQuestion", false);
            }
            session.setAttribute("questionCount", questionCount);
            session.setAttribute("quizName", quizName);


            Question currentQuestion = questionsByQuiz.remove(0);
            session.setAttribute("questionsByQuiz", questionsByQuiz);

            if (session.getAttribute("attempt") != null && selectedChoice != null) {
                selectedchoiceService.save(selectedChoice, (Attempt) session.getAttribute("attempt"));
            }

            if (selectedChoice != null && choiceService.getById(selectedChoice).isIscorrect()) {
                correctQuestionCounter++;
            }
            session.setAttribute("correctQuestionCounter", correctQuestionCounter);


            model.addAttribute("question", currentQuestion);
            model.addAttribute("choices", choiceService.choicesByQuestion(currentQuestion));

        }
        return "Question-and-choices";

    }

    @GetMapping("/getLogin")
    public String getLogin(HttpSession session, Model model) {
        if(session.getAttribute("user")!=null)
        {
            return "redirect:/";
        }
        model.addAttribute("user", session.getAttribute("user"));
        model.addAttribute("invalidSignin", session.getAttribute("invalidSignin"));
        session.removeAttribute("invalidSignin");
        setPrivilege(model, session);
        return "Login";
    }

    @GetMapping("/getRegister")
    public String getRegister(HttpSession session, Model model) {
        if(session.getAttribute("user")!=null)
        {
            return "redirect:/";
        }
        model.addAttribute("user", session.getAttribute("user"));
        model.addAttribute("invalidRegister", session.getAttribute("invalidRegister"));
        model.addAttribute("passwordDoNotMatch", session.getAttribute("passwordDoNotMatch"));
        session.removeAttribute("invalidRegister");
        session.removeAttribute("passwordDoNotMatch");
        setPrivilege(model, session);
        return "Sign-up";
    }

    @PostMapping("/processLogin")
    public String processLogin(@RequestParam String username,
                               @RequestParam String password,
                               HttpSession session) {
        User user = null;
        try {
            user = this.userService.findByUsernameAndPassword(username, password);
        } catch (InvalidCredentialsException e) {
            session.setAttribute("invalidSignin", true);
            return "redirect:/getLogin";
        }
        session.setAttribute("user", user);
        session.removeAttribute("invalidSignin");
        session.removeAttribute("passwordDoNotMatch");
        return "redirect:/";
    }

    @PostMapping("/processSignup")
    public String processSignup(@RequestParam String username,
                                @RequestParam(required = false) String full_name,
                                @RequestParam String password,
                                @RequestParam String password2,
                                HttpSession session) {


        try {
            if(password.equals(password2)){
                this.userService.registerUser(username, full_name, password);
            }
            else {
                session.setAttribute("passwordDoNotMatch", true);
                return "redirect:/getRegister";
            }
        } catch (UsernameAlreadyTakenException e) {
            session.setAttribute("invalidRegister", true);
            return "redirect:/getRegister";
        }
        return "redirect:/";
    }

    @GetMapping("/logOut")
    public String logOut(HttpSession session) {

        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/reports")
    public String reportList(Model model, HttpSession session)
    {
        if(redirectNonAdmin(session))
        {
            return "redirect:/";
        }
        model.addAttribute("user", session.getAttribute("user"));
        setPrivilege(model, session);


        return "reportList";
    }

    @GetMapping("/avgPoints")
    public String avgPoints(Model model, HttpSession session)
    {
        if(redirectNonAdmin(session))
        {
            return "redirect:/";
        }
        model.addAttribute("user", session.getAttribute("user"));
        setPrivilege(model, session);


        List<Avgpoenizakviz> list=avgpoenizakvizRepository.findAll();
        list.forEach(
                x -> {
                    /*DecimalFormat df = new DecimalFormat("###,##");
                    BigDecimal bigDecimal=x.getAvg();
                    x.setAvg(new BigDecimal(df.format(bigDecimal)));*/
                    x.setAvg(BigDecimal.valueOf(x.getAvg().intValue()));
                }

        );
        model.addAttribute("items", list);
        return "AvgPoeniZaKviz";

    }

    @GetMapping("/numbersReport")
    public String numbersReport(Model model, HttpSession session)
    {
        if(redirectNonAdmin(session))
        {
            return "redirect:/";
        }
        model.addAttribute("user", session.getAttribute("user"));
        setPrivilege(model, session);
        model.addAttribute("items", brturniribrigrachibrkvizovibrmedaliRepository.findAll());
        return "Brturniribrigrachibrkvizovibrmedali";
    }

    @GetMapping("/tournamentsReport")
    public String tournamentsReport(Model model, HttpSession session)
    {
        if(redirectNonAdmin(session))
        {
            return "redirect:/";
        }
        model.addAttribute("user", session.getAttribute("user"));
        setPrivilege(model, session);
        model.addAttribute("items", izveshtajzaturnirRepository.findAll());
        return "Izveshtajzaturniri";
    }

    @GetMapping("/medalRankings")
    public String medalRankings(Model model, HttpSession session)
    {
        if(redirectNonAdmin(session))
        {
            return "redirect:/";
        }
        model.addAttribute("user", session.getAttribute("user"));
        setPrivilege(model, session);
        model.addAttribute("items", korisnicirangiranisporedmedaliRepository.findAll());
        return "medalRankings";
    }

    @GetMapping("/correctQuestionQuizRankings")
    public String correctQuestionQuizRankings(Model model, HttpSession session)
    {
        if(redirectNonAdmin(session))
        {
            return "redirect:/";
        }
        model.addAttribute("user", session.getAttribute("user"));
        setPrivilege(model, session);
        model.addAttribute("items", kvizovirangiranisporedtochniprashanjaRepository.findAll());
        return "correctQuestionQuizRankings";
    }

    @GetMapping("/hardestWorkers")
    public String hardestWorkers(Model model, HttpSession session)
    {
        if(redirectNonAdmin(session))
        {
            return "redirect:/";
        }
        model.addAttribute("user", session.getAttribute("user"));
        setPrivilege(model, session);
        model.addAttribute("items", najangazhiranikorisniciRepository.findAll());
        return "hardestWorkers";
    }

    @GetMapping("/topQuizzes")
    public String topQuizzes(Model model, HttpSession session)
    {
        if(redirectNonAdmin(session))
        {
            return "redirect:/";
        }
        model.addAttribute("user", session.getAttribute("user"));
        setPrivilege(model, session);
        model.addAttribute("items", najigranikvizoviRepository.findAll());
        return "topQuizzes";
    }

    @GetMapping("/questionsRankedByCorrectAnswer")
    public String questionsRankedByQuiz(Model model, HttpSession session)
    {
        if(redirectNonAdmin(session))
        {
            return "redirect:/";
        }
        model.addAttribute("user", session.getAttribute("user"));
        setPrivilege(model, session);
        model.addAttribute("items", prashanjarangiranisporedtochniodgovoriRepository.findAll());
        return "questionsRankedByCorrectAnswer";
    }

    @GetMapping("/top5Solvers")
    public String top5Solvers(Model model, HttpSession session)
    {
        if(redirectNonAdmin(session))
        {
            return "redirect:/";
        }
        model.addAttribute("user", session.getAttribute("user"));
        setPrivilege(model, session);
        model.addAttribute("items", top5ReshavachiRepository.findAll());
        return "top5Solvers";
    }
    @GetMapping("/leaderboard")
    public String leaderboard(Model model, HttpSession session)
    {

        model.addAttribute("user", session.getAttribute("user"));
        setPrivilege(model, session);
        model.addAttribute("items", leaderboardRepository.findAll());
        return "leaderboard";
    }

    @GetMapping("/totalQuizzes3Months")
    public String totalQuizzes3Months(Model model, HttpSession session)
    {
        if(redirectNonAdmin(session))
        {
            return "redirect:/";
        }
        model.addAttribute("user", session.getAttribute("user"));
        setPrivilege(model, session);
        model.addAttribute("items", vkbrojreshenikvizovivo3MeseciRepositoryRepository.findAll());
        return "totalQuizzes3Months";
    }
}
